package nl.scalapino

import javafx.{ application => jfxa }
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.image.{ Image, ImageView }
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import javafx.{application => jfxa}

class HelloWorld1 extends jfxa.Application {

  def start(primaryStage: Stage) {
    val text = "Hello world! 1"
    primaryStage.setTitle(text)

    val background = new ImageView(new Image(getClass().
      getResourceAsStream("DigitalClock-background.png")));

    val root = new StackPane
    root.getChildren.addAll(new Label(text), background)

    primaryStage.setScene(new Scene(root, 480, 412))
    primaryStage.show()
  }

}

object HelloWorld1 {

  def main(args: Array[String]) {
    jfxa.Application.launch(classOf[HelloWorld1], args: _*)
  }
}