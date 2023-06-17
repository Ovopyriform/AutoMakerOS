package xyz.openautomaker.base.utils.Math.packing.core;

import java.awt.Dimension;
import java.util.ArrayList;

import xyz.openautomaker.base.BaseLookup;
import xyz.openautomaker.base.utils.Math.packing.primitives.MArea;
import xyz.openautomaker.environment.OpenAutoMakerEnv;

public class BinPacking {
	/**
	 * Entry point for the application. Applies the packing strategies to the
	 * provided pieces.
	 *
	 * @param pieces            pieces to be nested inside the bins.
	 * @param binDimension      dimensions for the generated bins.
	 * @param viewPortDimension dimensions of the view port for the bin images generation
	 * @return list of generated bins.
	 */
	public static Bin[] BinPackingStrategy(MArea[] pieces, Dimension binDimension, Dimension viewPortDimension) {
		System.out.println(".............Started computation of bin placements.............");
		ArrayList<Bin> bins = new ArrayList<Bin>();
		int nbin = 0;
//		boolean stillToPlace = true;
		MArea[] notPlaced = pieces;
		double t1 = System.currentTimeMillis();
//		while (stillToPlace) {
//			stillToPlace = false;

                // Removed loop in BinPackingStrategy as we only have 1 bin which is the size of the print bed.
                // This also fixes a bug where autolayout would infinitely run if used when an object does not fit on the bed.

			Bin bin = new Bin(binDimension);
			notPlaced = bin.BBCompleteStrategy(notPlaced);

			bin.compress();

			notPlaced = bin.dropPieces(notPlaced);

                        if (notPlaced.length > 0)
                        {
                            BaseLookup.getSystemNotificationHandler()
                                    .showErrorNotification(OpenAutoMakerEnv.getI18N().t("error.autolayoutModelTooBigTitle"), 
                                            OpenAutoMakerEnv.getI18N().t("error.autolayoutModelTooBigMessage"));
                        }
                        
			System.out.println("Bin " + (++nbin) + " generated");
			bins.add(bin);
//			if (notPlaced.length > 0)
//				stillToPlace = true;
//		}
		double t2 = System.currentTimeMillis();
		System.out.println();
		System.out.println("Number of used bins: " + nbin);
		System.out.println("Computation time:" + ((t2 - t1) / 1000) / 60 + " minutes");
		System.out.println();
		return bins.toArray(new Bin[0]);
	}

}
