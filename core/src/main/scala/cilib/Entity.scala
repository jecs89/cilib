package cilib

import scala.language.higherKinds
import Predef.{any2stringadd => _, _}
import scalaz._
import Scalaz._

// Transformer of some sort, over the type F
sealed abstract class Position[F[_], A] {
  import Position._

  def pos =
    this match {
      case Point(x)  => x
      case Solution(x, _) => x
    }

  def fit: Option[Fit] =
    this match {
      case Point(_)  => None
      case Solution(_, f) => Some(f)
    }

  def map[B](f: A => B)(implicit F: Monad[F]): Position[F, B] =
    Point(pos map f)

  def flatMap[B](f: A => Position[F, B])(implicit F: Monad[F]): Position[F, B] =
    Point(F.bind(pos)(f(_).pos))

  def zip[B](other: Position[F, B])(implicit F: Zip[F]): Position[F, (A, B)] =
    Point(F.zip(pos, other.pos))

  def traverse[G[_]: Applicative, B](f: A => G[B])(implicit F: Traverse[F]): G[Position[F, B]] =
    F.traverse(pos)(f).map(Point(_))

  def eval(problem: F[A] => Fit) =
    this match {
      case Point(x) => Solution(x, problem(x))
      case Solution(_ ,_) => this
    }
}

object Position {
  import spire.algebra._
  import spire.math.{ Interval => _, _ }
  import spire.implicits._

  implicit def positionInstances[F[_]](implicit F0: Monad[F], F1: Zip[F]): Bind[({type λ[α] = Position[F,α]})#λ] with Zip[({type λ[α] = Position[F,α]})#λ] =
    new Bind[({type λ[α] = Position[F,α]})#λ] with Zip[({type λ[α] = Position[F,α]})#λ] {
      def point[A](a: => A): cilib.Position[F,A] =
        Point(Applicative[F].point(a))

      def map[A, B](fa: Position[F, A])(f: A => B): Position[F, B] =
        fa map f

      def bind[A, B](fa: Position[F, A])(f: A => Position[F,B]): Position[F, B] =
        fa flatMap f

      def zip[A, B](a: => Position[F, A], b: => Position[F, B]): Position[F, (A, B)] =
        a zip b
    }

  private final case class Point[F[_], A](x: F[A]) extends Position[F, A]
  private final case class Solution[F[_], A](x: F[A], f: Fit) extends Position[F, A]

  implicit class ToPositionVectorOps[F[_], A: Fractional](x: Position[F, A]) {
    def + (other: Position[F, A])(implicit Z: Zip[F], F: Functor[F]): Position[F, A] = Point {
      Z.zipWith(x.pos, other.pos)(_ + _)
    }
    def - (other: Position[F, A])(implicit Z: Zip[F], F: Functor[F]): Position[F, A] = Point(Z.zipWith(x.pos, other.pos)(_ - _))
    /*def * (other: Position[F, A])(implicit F: Zip[F]) = Solution(x.pos.zipWith(other.pos)((a, ob) => ob.map(_ * a).getOrElse(a))._2) */
    def *:(scalar: A)(implicit F: Functor[F]): Position[F, A] = Point(x.pos.map(_ * scalar))
  }

  implicit def positionFitness[F[_], A] = new Fitness[Position[F, A]] {
    def fitness(a: Position[F, A]) =
      a.fit
  }

  // Smart constructor
  def apply[A](xs: List[A]): Position[List, A] =
    Point(xs)

  // Construction utilities
  def mkPos[A](i: List[Interval[A]])(implicit N: Numeric[A]) =
    i traverse (_.fold((x, y) => Dist.uniform(N.toDouble(x), N.toDouble(y)))) map (Position(_))

  def mkColl[A: Numeric](i: List[Interval[A]], n: Int) =
    mkPos(i) replicateM n
}

sealed trait Bound[A]
case class Closed[A](a: A) extends Bound[A]
case class Open[A](a: A) extends Bound[A]

final class Interval[A](val lower: Bound[A], val upper: Bound[A]) {

  def fold[B](f: (A, A) => B): B =
    (lower, upper) match {
      case (Closed(a), Closed(b)) => f(a, b)
      case (Closed(a), Open(b)) => f(a, b)
      case (Open(a), Closed(b)) => f(a, b)
      case (Open(a), Open(b)) => f(a, b)
    }

  def ^(n: Int): List[Interval[A]] =
    (1 to n).map(_ => this).toList

}

object Interval {
  def apply[A](l: Bound[A], r: Bound[A]) =
    new Interval(l, r)

  def closed[A](lower: A, upper: A) =
    new Interval(Closed(lower), Closed(upper))
}