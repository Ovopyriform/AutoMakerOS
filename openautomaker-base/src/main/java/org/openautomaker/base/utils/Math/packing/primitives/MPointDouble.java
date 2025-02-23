package org.openautomaker.base.utils.Math.packing.primitives;

import java.awt.geom.Point2D;


/**
 * Implementation of Point2D.Double.
 *
 * @author Moises Baly
 */
public class MPointDouble extends Point2D.Double implements Comparable<MPointDouble> {

	public MPointDouble(double x, double y) {
		super(x, y);
	}

	/**
	 * Comparable method
	 */
	@Override
	public int compareTo(MPointDouble o) {
		if (this.x < o.x)
			return -1;
		if (this.x > o.x)
			return 1;
		if (this.y < o.y)
			return -1;
		if (this.y > o.y)
			return 1;
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		MPointDouble pobj = (MPointDouble) obj;
		return (this.x == pobj.x && this.y == pobj.y);
	}
}
