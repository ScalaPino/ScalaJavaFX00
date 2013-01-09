package nl.scalapino

import javafx.{application => jfxa}
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import javafx.{application => jfxa}

class HelloWorld extends jfxa.Application {

  override def start(primaryStage: Stage) {
    val text = "Hello world!"
    primaryStage.setTitle(text)

    val root = new StackPane
    root.getChildren.add(new Label(text))

    primaryStage.setScene(new Scene(root, 300, 300))
    primaryStage.show()
  }
}

object HelloWorld {
  def main(args: Array[String]) {
    jfxa.Application.launch(classOf[HelloWorld], args: _*)
  }
}