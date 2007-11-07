/*
 * Created on 2005/04/23
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.sourceforge.cilib.neuralnetwork.testarea;

import net.sourceforge.cilib.neuralnetwork.foundation.NNError;
import net.sourceforge.cilib.neuralnetwork.foundation.NeuralNetworkController;
import net.sourceforge.cilib.neuralnetwork.foundation.NeuralNetworkProblem;
import net.sourceforge.cilib.neuralnetwork.generic.GenericTopology;
import net.sourceforge.cilib.neuralnetwork.generic.LayeredGenericTopology;
import net.sourceforge.cilib.neuralnetwork.generic.Weight;
import net.sourceforge.cilib.neuralnetwork.generic.datacontainers.DynamicPatternSelectionData;
import net.sourceforge.cilib.neuralnetwork.generic.datacontainers.RandomDistributionStrategy;
import net.sourceforge.cilib.neuralnetwork.generic.datacontainers.StandardPattern;
import net.sourceforge.cilib.neuralnetwork.generic.errorfunctions.ClassificationErrorReal;
import net.sourceforge.cilib.neuralnetwork.generic.errorfunctions.MSEErrorFunction;
import net.sourceforge.cilib.neuralnetwork.generic.evaluationmediators.SAILAEvaluationMediator;
import net.sourceforge.cilib.neuralnetwork.generic.topologybuilders.FFNNgenericTopologyBuilder;
import net.sourceforge.cilib.neuralnetwork.generic.trainingstrategies.FFNN_GD_TrainingStrategy;
import net.sourceforge.cilib.neuralnetwork.generic.trainingstrategies.SquaredErrorFunction;
import net.sourceforge.cilib.stoppingcondition.MaximumIterations;
import net.sourceforge.cilib.type.types.Real;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 * @author stefanv
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TestErrorINCLearnWithTrainer {

	
	
public static void main(String[] args) {
		
		int[] sizes = new int[3];
		sizes[0] = 3;
		sizes[1] = 6;
		sizes[2] = 1;
		
		Weight base= new Weight(new Real(0.5));
		
		
	//	GenericTopology topo = new GenericTopology(new FFNNStaticTopologyBuilder());
	//	GenericTopology topo = new GenericTopology();
		GenericTopology topo = new LayeredGenericTopology();
		FFNNgenericTopologyBuilder builder = new FFNNgenericTopologyBuilder();
		builder.setPrototypeWeight(base);
		builder.addLayer(3);
		builder.addLayer(6);
		builder.addLayer(1);
		topo.setTopologyBuilder(builder);
				
		FFNN_GD_TrainingStrategy trainer = new FFNN_GD_TrainingStrategy();
		trainer.setDelta(new SquaredErrorFunction());
		trainer.setTopology(topo);
		trainer.setMomentum(0.9);
		trainer.setLearningRate(0.1);
		
		DynamicPatternSelectionData data = null;
		
		
		data = new DynamicPatternSelectionData();
		RandomDistributionStrategy distributor = new RandomDistributionStrategy();
		distributor.setFile("d:\\Stefan University\\masters\\datasets\\F2.txt");
		distributor.setNoInputs(2);
		distributor.setPercentTrain(1);
		distributor.setPercentGen(20);
		distributor.setPercentVal(20);
		distributor.setPercentCan(59);
		data.setDistributor(distributor);
		data.setTopology(topo);
		
		
		NNError err = new MSEErrorFunction();
		err.setNoOutputs(1);
		err.setNoPatterns(1000);		//direct settings wont work in XML
		NNError err1 = new ClassificationErrorReal();
		
		SAILAEvaluationMediator eval = new SAILAEvaluationMediator();
		eval.setTopology(topo);
		eval.setData(data);
		eval.addPrototypError(err);
		eval.addPrototypError(err1);
		eval.setTrainer(trainer);
		
		NeuralNetworkProblem NNprob = new NeuralNetworkProblem();
		NNprob.setEvaluationStrategy(eval);
				
		NeuralNetworkController NNControl = new NeuralNetworkController();
		NNControl.setProblem(NNprob);
		
		NNControl.addStoppingCondition(new MaximumIterations(1000));
//		add stopping kondisie
		
		System.out.println("Configuration completed...");
//		-----------------------------------------------------------------------------------------------------------

NNControl.initialise();
//needed

System.out.println("TEST SETUP: data stats before training:\n\n");

System.out.println("candidate set size      : " + data.getCandidateSetSize());
System.out.println("training set size       : " + data.getTrainingSetSize());
System.out.println("generalisation set size : " + data.getGeneralisationSetSize());
System.out.println("validation set size     : " + data.getValidationSetSize());
System.out.println("Candidate set           : " + data.getCandidateSetSize());


NNControl.run();
////run die stuff
		
		

Vector in = new Vector();

in.add(new Real(0.5));
in.add(new Real(1.234));

StandardPattern p = new StandardPattern(in, null);

Vector result = topo.evaluate(p);

System.out.println("test result f(0.5) = 0.25  -->  : " + ((Real)result.get(0)).getReal());

System.out.println("data stats:\n\n");

System.out.println("candidate set size      : " + data.getCandidateSetSize());
System.out.println("training set size       : " + data.getTrainingSetSize());
System.out.println("generalisation set size : " + data.getGeneralisationSetSize());
System.out.println("validation set size     : " + data.getValidationSetSize());


		
		
		
		
		
		
		
		
		
		
	}
}
