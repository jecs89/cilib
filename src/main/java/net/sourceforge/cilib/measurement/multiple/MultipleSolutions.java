/*
 * MultipleSolutions.java
 *
 * Created on January 17, 2003, 4:54 PM
 *
 * Copyright (C) 2003 - Clive Naicker
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package net.sourceforge.cilib.measurement.multiple;

import java.util.List;

import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.measurement.Measurement;
import net.sourceforge.cilib.problem.OptimisationSolution;
import net.sourceforge.cilib.type.types.Type;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 * 
 * @author Edwin Peer
 */
public class MultipleSolutions implements Measurement {
	private static final long serialVersionUID = 1617755270627315980L;

	public MultipleSolutions() {
	}
	
	public MultipleSolutions(MultipleSolutions copy) {
	}
	
	public MultipleSolutions clone() {
		return new MultipleSolutions(this);
	}

	public String getDomain() {
		return "T";
	}
	
	public Type getValue() {		
		Vector v = new Vector();
		List<OptimisationSolution> solutions = Algorithm.get().getSolutions();
		
		for (OptimisationSolution solution : solutions) {
			v.append((Vector) solution.getPosition());
		}
		
		return v;
	}
	
}
