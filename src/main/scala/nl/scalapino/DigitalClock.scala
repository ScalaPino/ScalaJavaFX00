package nl.scalapino
/**
 *
 */

import java.util.Calendar

import javafx.animation.{ Animation, KeyFrame, Timeline }
import javafx.{ application => jfxa }
import javafx.event.{ ActionEvent, EventHandler }
import javafx.scene.{ Group, Parent, Scene }
import javafx.scene.effect.{ Effect, Glow, InnerShadow }
import javafx.scene.image.{ Image, ImageView }
import javafx.scene.paint.Color
import javafx.scene.shape.{ Circle, Polygon }
import javafx.scene.transform.{ Scale, Shear }
import javafx.stage.Stage
import javafx.util.Duration

/**
 *
 * A digital clock application that demonstrates JavaFX animation, images, and
 * effects.
 *
 * @see javafx.scene.effect.Glow
 * @see javafx.scene.shape.Polygon
 * @see javafx.scene.transform.Shear
 * @resource DigitalClock-background.png
 */

class DigitalClock extends jfxa.Application {
  def start(primaryStage: Stage) = DigitalClock.start(primaryStage)
  //def play() {DigitalClock.clock.play()}
  override def stop() { DigitalClock.clock.stop() }
}

object DigitalClock extends jfxa.Application {

  val clock = new Clock(Color.ORANGERED, Color.rgb(50, 50, 50))

  private def init(primaryStage: Stage) {
    val root = new Group
    primaryStage.setResizable(false);
    primaryStage.setScene(new Scene(root, 480, 412));
    // add digital clock
    clock.setLayoutX(45)
    clock.setLayoutY(186)
    clock.getTransforms().add(new Scale(0.83f, 0.83f, 0, 0));
    // add background and clock to sample
    root.getChildren().addAll(new ImageView(new Image(getClass()
      .getResourceAsStream("..\\..\\resources\\DigitalClock-background.png"))), clock)
  }

  // def play() {clock.play()}
  // override def stop() {clock.stop()}

  /**
   *
   * Clock made of 6 of the Digit classes for hours, minutes and seconds.
   */
  class Clock(onColor: Color, offColor: Color) extends Parent {
    private val calendar = Calendar.getInstance()
    private var digits: Array[Digit] = Array(null, null, null, null, null, null)
    private var delayTimeline: Timeline = _
    private var secondTimeline: Timeline = _

    //		def Clock( onColor: Color, offColor:Color ) {
    // create effect for on LEDs
    private val onEffect = new Glow(1.7f)
    onEffect.setInput(new InnerShadow())
    // create effect for on dot LEDs
    private val onDotEffect = new Glow(1.7f)
    onDotEffect.setInput(new InnerShadow(5, Color.BLACK))
    // create effect for off LEDs
    private val offEffect = new InnerShadow()
    // create digits
    for (i <- 0 until digits.length) {
      val digit = new Digit(onColor, offColor, onEffect, offEffect)
      digit.setLayoutX(i * 80 + ((i + 1) % 2) * 20)
      digits(i) = digit;
      getChildren().add(digit)
    }

    // Create dots
    private val dots = new Group(
      new Circle(80 + 54 + 20, 44, 6, onColor),
      new Circle(80 + 54 + 17, 64, 6, onColor),
      new Circle((80 * 3) + 54 + 20, 44, 6, onColor),
      new Circle((80 * 3) + 54 + 17, 64, 6, onColor));
    dots.setEffect(onDotEffect)
    getChildren().add(dots)

    // update digits to current time and start timer to update every second

    refreshClocks()

    private def refreshClocks() {
      calendar.setTimeInMillis(System.currentTimeMillis());
      val hours = calendar.get(Calendar.HOUR_OF_DAY);
      val minutes = calendar.get(Calendar.MINUTE);
      val seconds = calendar.get(Calendar.SECOND);

      digits(0).showNumber(hours / 10)
      digits(1).showNumber(hours % 10)
      digits(2).showNumber(minutes / 10)
      digits(3).showNumber(minutes % 10)
      digits(4).showNumber(seconds / 10)
      digits(5).showNumber(seconds % 10)
    }

    def play() {
      // wait till start of next second then start a timeline to call
      // refreshClocks() every second

      delayTimeline = new Timeline()
      delayTimeline.getKeyFrames().add(
        new KeyFrame(new Duration(1000 - (System.currentTimeMillis() % 1000)),
          new EventHandler[ActionEvent]() {
            override def handle(event: ActionEvent) {
              if (secondTimeline != null) {
                secondTimeline.stop();
              }

              secondTimeline = new Timeline();
              secondTimeline.setCycleCount(Animation.INDEFINITE)
              secondTimeline.getKeyFrames().add(
                new KeyFrame(
                  Duration.seconds(1),
                  new EventHandler[ActionEvent]() {
                    override def handle(event: ActionEvent) {
                      refreshClocks()
                    }
                  }))
              secondTimeline.play()
            }
          }))
      delayTimeline.play();
    }

    def stop() {
      delayTimeline.stop()
      if (secondTimeline != null) secondTimeline.stop();
    }

  } // class Clock(onColor: Color, offColor: Color)

  /**
   *
   * Simple 7 segment LED style digit. It supports the numbers 0 through 9.
   */

  class Digit(pOnColor: Color, pOffColor: Color, pOnEffect: Effect, pOffEffect: Effect)
    extends Parent {
    val polygons =
      Array(
        new Polygon(2, 0, 52, 0, 42, 10, 12, 10),
        new Polygon(12, 49, 42, 49, 52, 54, 42, 59, 12f, 59f, 2f, 54f),
        new Polygon(12, 98, 42, 98, 52, 108, 2, 108),
        new Polygon(0, 2, 10, 12, 10, 47, 0, 52),
        new Polygon(44, 12, 54, 2, 54, 52, 44, 47),
        new Polygon(0, 56, 10, 61, 10, 96, 0, 106),
        new Polygon(44, 61, 54, 56, 54, 106, 44, 96))

    getChildren.addAll(polygons(0),
      polygons(1),
      polygons(2),
      polygons(3),
      polygons(4),
      polygons(5),
      polygons(6))

    getTransforms().add(new Shear(-0.1, 0))
    //showNumber(0)

    def showNumber(num: Int) {
      val number = if (num < 0 || num > 9) 0 else num // default to 0 for non-valid numbers
      for (i <- 0 until 7) {
        polygons(i).setFill(if (Digit.DIGIT_COMBINATIONS(number)(i)) pOnColor else pOffColor)
        polygons(i).setEffect(if (Digit.DIGIT_COMBINATIONS(number)(i)) pOnEffect else pOffEffect)
      }
    }
  }

  object Digit {

    val DIGIT_COMBINATIONS =
      Array(
        Array(true, false, true, true, true, true, true),
        Array(false, false, false, false, true, false, true),
        Array(true, true, true, false, true, true, false),
        Array(true, true, true, false, true, false, true),
        Array(false, true, false, true, true, false, true),
        Array(true, true, true, true, false, false, true),
        Array(true, true, true, true, false, true, true),
        Array(true, false, false, false, true, false, true),
        Array(true, true, true, true, true, true, true),
        Array(true, true, true, true, true, false, true))

  } //Object Digit

  def start(primaryStage: Stage) = {
    init(primaryStage);
    primaryStage.show();
    clock.play();
  }
  def main(args: Array[String]): Unit = {
    jfxa.Application.launch(classOf[DigitalClock], args: _*)
  }
}