package jfxtras.styles.jmetro8;

import com.sun.javafx.scene.control.behavior.SliderBehavior;

import javafx.animation.Transition;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.util.StringConverter;

/**
 * Created by pedro_000 on 3/30/2014.
 */
public class FilledSliderSkin extends SkinBase<Slider> {
	private final SliderBehavior sliderBehavior;
	/**
	 * Track if slider is vertical/horizontal and cause re layout
	 */
	//    private boolean horizontal;
	private NumberAxis tickLine = null;
	private double trackToTickGap = 2;

	private boolean showTickMarks;
	private double thumbWidth;
	private double thumbHeight;

	private double trackStart;
	private double trackLength;
	private double thumbTop;
	private double thumbLeft;
	private double preDragThumbPos;
	private Point2D dragStart; // in skin coordinates

	private StackPane thumb;
	private StackPane track;
	private StackPane fill;
	private boolean trackClicked = false;
	//    private double visibleAmount = 16;

	public FilledSliderSkin(Slider slider) {
		super(slider);
		sliderBehavior = new SliderBehavior(slider);
		initialize();
		slider.requestLayout();

		registerChangeListener(slider.minProperty(), e -> updateMin());
		registerChangeListener(slider.maxProperty(), e -> updateMax());
		registerChangeListener(slider.valueProperty(), e -> updateValue());
		registerChangeListener(slider.orientationProperty(), e -> updateOrientation());
		registerChangeListener(slider.showTickMarksProperty(), e -> updateTickMarksAndLabels());
		registerChangeListener(slider.showTickLabelsProperty(), e -> updateTickMarksAndLabels());
		registerChangeListener(slider.majorTickUnitProperty(), e -> updateMajorTickUnit());
		registerChangeListener(slider.minorTickCountProperty(), e -> updateMinorTickCount());
		registerChangeListener(slider.labelFormatterProperty(), e -> updateTickLabelFormatter());
	}

	private void updateOrientation() {
		Slider slider = getSkinnable();
		if (showTickMarks && tickLine != null) {
			tickLine.setSide(slider.getOrientation() == Orientation.VERTICAL ? Side.RIGHT : (slider.getOrientation() == null) ? Side.RIGHT : Side.BOTTOM);
		}
		slider.requestLayout();
	}

	private void updateValue() {
		positionThumb(trackClicked);
	}

	private void updateMin() {
		if (showTickMarks && tickLine != null) {
			tickLine.setLowerBound(getSkinnable().getMin());
		}
		getSkinnable().requestLayout();
	}

	private void updateMax() {
		if (showTickMarks && tickLine != null) {
			tickLine.setUpperBound(getSkinnable().getMax());
		}
		getSkinnable().requestLayout();
	}

	private void updateTickMarksAndLabels() {
		setShowTickMarks(getSkinnable().isShowTickMarks(), getSkinnable().isShowTickLabels());
		getSkinnable().requestLayout();
	}

	private void updateMajorTickUnit() {
		if (tickLine != null) {
			tickLine.setTickUnit(getSkinnable().getMajorTickUnit());
			getSkinnable().requestLayout();
		}
	}

	private void updateMinorTickCount() {
		if (tickLine != null) {
			tickLine.setMinorTickCount(Math.max(getSkinnable().getMinorTickCount(), 0) + 1);
			getSkinnable().requestLayout();
		}
	}

	private void updateTickLabelFormatter() {
		if (tickLine != null) {
			if (getSkinnable().getLabelFormatter() == null) {
				tickLine.setTickLabelFormatter(null);
			}
			else {
				tickLine.setTickLabelFormatter(stringConverterWrapper);
				tickLine.requestAxisLayout();
			}
		}
	}

	private void initialize() {
		thumb = new StackPane();
		thumb.getStyleClass().setAll("thumb");
		track = new StackPane();
		track.getStyleClass().setAll("track");
		fill = new StackPane();
		fill.getStyleClass().setAll("fill");
		//        horizontal = getSkinnable().isVertical();

		getChildren().clear();
		getChildren().addAll(track, fill, thumb);
		setShowTickMarks(getSkinnable().isShowTickMarks(), getSkinnable().isShowTickLabels());

		track.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				mousePressedOnTrack(me);
			}
		});
		track.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				mouseDraggedOnTrack(me);
			}
		});
		fill.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				mousePressedOnTrack(me);
			}
		});
		fill.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				mouseDraggedOnTrack(me);
			}
		});

		thumb.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				sliderBehavior.thumbPressed(me, 0.0f);
				dragStart = thumb.localToParent(me.getX(), me.getY());
				preDragThumbPos = (getSkinnable().getValue() - getSkinnable().getMin()) / (getSkinnable().getMax() - getSkinnable().getMin());
			}
		});

		thumb.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				sliderBehavior.thumbReleased(me);
			}
		});

		thumb.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				Point2D cur = thumb.localToParent(me.getX(), me.getY());
				double dragPos = (getSkinnable().getOrientation() == Orientation.HORIZONTAL)
						? cur.getX() - dragStart.getX()
						: -(cur.getY() - dragStart.getY());
				sliderBehavior.thumbDragged(me, preDragThumbPos + dragPos / trackLength);
			}
		});
	}

	private void mousePressedOnTrack(MouseEvent mouseEvent) {
		if (!thumb.isPressed()) {
			if (getSkinnable().getOrientation() == Orientation.HORIZONTAL) {
				sliderBehavior.trackPress(mouseEvent, (mouseEvent.getX() / trackLength));
			}
			else {
				sliderBehavior.trackPress(mouseEvent, (mouseEvent.getY() / trackLength));
			}
		}
	}

	private void mouseDraggedOnTrack(MouseEvent mouseEvent) {
		if (!thumb.isPressed()) {
			if (getSkinnable().getOrientation() == Orientation.HORIZONTAL) {
				sliderBehavior.trackPress(mouseEvent, (mouseEvent.getX() / trackLength));
			}
			else {
				sliderBehavior.trackPress(mouseEvent, (mouseEvent.getY() / trackLength));
			}
		}
	}

	StringConverter<Number> stringConverterWrapper = new StringConverter<>() {
		Slider slider = getSkinnable();

		@Override
		public String toString(Number object) {
			return (object != null) ? slider.getLabelFormatter().toString(object.doubleValue()) : "";
		}

		@Override
		public Number fromString(String string) {
			return slider.getLabelFormatter().fromString(string);
		}
	};

	private void setShowTickMarks(boolean ticksVisible, boolean labelsVisible) {
		showTickMarks = (ticksVisible || labelsVisible);
		Slider slider = getSkinnable();
		if (showTickMarks) {
			if (tickLine == null) {
				tickLine = new NumberAxis();
				tickLine.setAutoRanging(false);
				tickLine.setSide(slider.getOrientation() == Orientation.VERTICAL ? Side.RIGHT : (slider.getOrientation() == null) ? Side.RIGHT : Side.BOTTOM);
				tickLine.setUpperBound(slider.getMax());
				tickLine.setLowerBound(slider.getMin());
				tickLine.setTickUnit(slider.getMajorTickUnit());
				tickLine.setTickMarkVisible(ticksVisible);
				tickLine.setTickLabelsVisible(labelsVisible);
				tickLine.setMinorTickVisible(ticksVisible);
				// add 1 to the slider minor tick count since the axis draws one
				// less minor ticks than the number given.
				tickLine.setMinorTickCount(Math.max(slider.getMinorTickCount(), 0) + 1);
				if (slider.getLabelFormatter() != null) {
					tickLine.setTickLabelFormatter(stringConverterWrapper);
				}
				getChildren().clear();
				getChildren().addAll(tickLine, track, fill, thumb);
			}
			else {
				tickLine.setTickLabelsVisible(labelsVisible);
				tickLine.setTickMarkVisible(ticksVisible);
				tickLine.setMinorTickVisible(ticksVisible);
			}
		}
		else {
			getChildren().clear();
			getChildren().addAll(track, fill, thumb);
			//            tickLine = null;
		}

		getSkinnable().requestLayout();
	}

	/**
	 * Called when ever either min, max or value changes, so thumb's layoutX, Y is recomputed.
	 */
	void positionThumb(final boolean animate) {
		Slider s = getSkinnable();
		if (s.getValue() > s.getMax()) {
			return;// this can happen if we are bound to something
		}
		boolean horizontal = s.getOrientation() == Orientation.HORIZONTAL;
		final double endX = (horizontal) ? trackStart + (((trackLength * ((s.getValue() - s.getMin()) / (s.getMax() - s.getMin()))) - thumbWidth / 2)) : thumbLeft;
		final double endY = (horizontal) ? thumbTop
				: snappedTopInset() + trackLength - (trackLength * ((s.getValue() - s.getMin()) / (s.getMax() - s.getMin()))); //  - thumbHeight/2

		if (animate) {
			// lets animate the thumb transition
			final double startX = thumb.getLayoutX();
			final double startY = thumb.getLayoutY();
			Transition transition = new Transition() {
				{
					setCycleDuration(Duration.millis(200));
				}

				@Override
				protected void interpolate(double frac) {
					if (!Double.isNaN(startX)) {
						thumb.setLayoutX(startX + frac * (endX - startX));
					}
					if (!Double.isNaN(startY)) {
						thumb.setLayoutY(startY + frac * (endY - startY));
					}
				}
			};
			transition.play();
		}
		else {
			thumb.setLayoutX(endX);
			thumb.setLayoutY(endY);
		}
	}

	@Override
	protected void layoutChildren(final double x, final double y,
			final double w, final double h) {
		// calculate the available space
		// resize thumb to preferred size
		thumbWidth = snapSize(thumb.prefWidth(-1));
		thumbHeight = snapSize(thumb.prefHeight(-1));
		thumb.resize(thumbWidth, thumbHeight);
		// we are assuming the is common radius's for all corners on the track
		double trackRadius = track.getBackground() == null ? 0
				: track.getBackground().getFills().size() > 0
						? track.getBackground().getFills().get(0).getRadii().getTopLeftHorizontalRadius()
						: 0;

		if (getSkinnable().getOrientation() == Orientation.HORIZONTAL) {
			double tickLineHeight = (showTickMarks) ? tickLine.prefHeight(-1) : 0;
			double trackHeight = snapSize(track.prefHeight(-1));
			double trackAreaHeight = Math.max(trackHeight, thumbHeight);
			double totalHeightNeeded = trackAreaHeight + ((showTickMarks) ? trackToTickGap + tickLineHeight : 0);
			double startY = y + ((h - totalHeightNeeded) / 2); // center slider in available height vertically
			trackLength = snapSize(w - thumbWidth);
			trackStart = snapPosition(x + (thumbWidth / 2));
			double trackTop = (int) (startY + ((trackAreaHeight - trackHeight) / 2));
			thumbTop = (int) (startY + ((trackAreaHeight - thumbHeight) / 2));

			positionThumb(false);
			// layout track
			track.resizeRelocate((int) (trackStart - trackRadius),
					trackTop,
					(int) (trackLength + trackRadius + trackRadius),
					trackHeight);
			//            fill.resizeRelocate((int) (trackStart - trackRadius),
			//                                trackTop,
			//                                ((int) trackStart - trackRadius) + thumb.getLayoutX(),
			//                                trackHeight);
			Slider s = getSkinnable();
			final double width = ((trackLength * ((s.getValue() - s.getMin()) / (s.getMax() - s.getMin()))) - thumbWidth / 2);
			fill.resizeRelocate((int) (trackStart - trackRadius),
					trackTop,
					width,
					trackHeight);
			// layout tick line
			if (showTickMarks) {
				tickLine.setLayoutX(trackStart);
				tickLine.setLayoutY(trackTop + trackHeight + trackToTickGap);
				tickLine.resize(trackLength, tickLineHeight);
				tickLine.requestAxisLayout();
			}
			else {
				if (tickLine != null) {
					tickLine.resize(0, 0);
					tickLine.requestAxisLayout();
				}
				tickLine = null;
			}
		}
		else {
			double tickLineWidth = (showTickMarks) ? tickLine.prefWidth(-1) : 0;
			double trackWidth = snapSize(track.prefWidth(-1));
			double trackAreaWidth = Math.max(trackWidth, thumbWidth);
			double totalWidthNeeded = trackAreaWidth + ((showTickMarks) ? trackToTickGap + tickLineWidth : 0);
			double startX = x + ((w - totalWidthNeeded) / 2); // center slider in available width horizontally
			trackLength = snapSize(h - thumbHeight);
			trackStart = snapPosition(y + (thumbHeight / 2));
			double trackLeft = (int) (startX + ((trackAreaWidth - trackWidth) / 2));
			thumbLeft = (int) (startX + ((trackAreaWidth - thumbWidth) / 2));

			positionThumb(false);
			// layout track
			track.resizeRelocate(trackLeft,
					(int) (trackStart - trackRadius),
					trackWidth,
					(int) (trackLength + trackRadius + trackRadius));
			fill.resizeRelocate(trackLeft,
					(int) (trackStart - trackRadius),
					trackWidth,
					((int) trackStart - trackRadius) + thumb.getLayoutY());
			// layout tick line
			if (showTickMarks) {
				tickLine.setLayoutX(trackLeft + trackWidth + trackToTickGap);
				tickLine.setLayoutY(trackStart);
				tickLine.resize(tickLineWidth, trackLength);
				tickLine.requestAxisLayout();
			}
			else {
				if (tickLine != null) {
					tickLine.resize(0, 0);
					tickLine.requestAxisLayout();
				}
				tickLine = null;
			}
		}
	}

	double minTrackLength() {
		return 2 * thumb.prefWidth(-1);
	}

	@Override
	protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		final Slider s = getSkinnable();
		if (s.getOrientation() == Orientation.HORIZONTAL) {
			return (leftInset + minTrackLength() + thumb.minWidth(-1) + rightInset);
		}
		else {
			return (leftInset + thumb.prefWidth(-1) + rightInset);
		}
	}

	@Override
	protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		final Slider s = getSkinnable();
		if (s.getOrientation() == Orientation.HORIZONTAL) {
			return (topInset + thumb.prefHeight(-1) + bottomInset);
		}
		else {
			return (topInset + minTrackLength() + thumb.prefHeight(-1) + bottomInset);
		}
	}

	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		final Slider s = getSkinnable();
		if (s.getOrientation() == Orientation.HORIZONTAL) {
			if (showTickMarks) {
				return Math.max(140, tickLine.prefWidth(-1));
			}
			else {
				return 140;
			}
		}
		else {
			return leftInset + Math.max(thumb.prefWidth(-1), track.prefWidth(-1)) + ((showTickMarks) ? (trackToTickGap + tickLine.prefWidth(-1)) : 0) + rightInset;
		}
	}

	@Override
	protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		final Slider s = getSkinnable();
		if (s.getOrientation() == Orientation.HORIZONTAL) {
			return topInset + Math.max(thumb.prefHeight(-1), track.prefHeight(-1)) + ((showTickMarks) ? (trackToTickGap + tickLine.prefHeight(-1)) : 0) + bottomInset;
		}
		else {
			if (showTickMarks) {
				return Math.max(140, tickLine.prefHeight(-1));
			}
			else {
				return 140;
			}
		}
	}

	@Override
	protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		if (getSkinnable().getOrientation() == Orientation.HORIZONTAL) {
			return Double.MAX_VALUE;
		}
		else {
			return getSkinnable().prefWidth(-1);
		}
	}

	@Override
	protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		if (getSkinnable().getOrientation() == Orientation.HORIZONTAL) {
			return getSkinnable().prefHeight(width);
		}
		else {
			return Double.MAX_VALUE;
		}
	}
}
