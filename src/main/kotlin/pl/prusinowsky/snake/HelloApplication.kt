package pl.prusinowsky.snake

import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.KeyCode
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Stage
import java.util.*

class HelloApplication : Application() {
    private val width = 20
    private val height = 15
    private val tileSize = 30.0
    private val snake = LinkedList<Pair<Int, Int>>()
    private var direction = Direction.RIGHT
    private var foodX = 0
    private var foodY = 0
    private val random = Random()
    private var isGameOver = false

    private enum class Direction {
        UP, DOWN, LEFT, RIGHT
    }

    override fun start(primaryStage: Stage) {
        val root = Pane()
        val canvas = Canvas(width * tileSize, height * tileSize)
        val gc = canvas.graphicsContext2D
        root.children.add(canvas)

        val scene = Scene(root)
        scene.setOnKeyPressed { e ->
            when (e.code) {
                KeyCode.UP -> if (direction != Direction.DOWN) direction = Direction.UP
                KeyCode.DOWN -> if (direction != Direction.UP) direction = Direction.DOWN
                KeyCode.LEFT -> if (direction != Direction.RIGHT) direction = Direction.LEFT
                KeyCode.RIGHT -> if (direction != Direction.LEFT) direction = Direction.RIGHT
                else -> {}
            }
        }

        primaryStage.title = "Snake Game"
        primaryStage.scene = scene

        generateFood()
        snake.add(Pair(5, 5))

        val timer = object : AnimationTimer() {
            private var lastUpdate = 0L

            override fun handle(now: Long) {
                if (now - lastUpdate >= 100000000) {
                    if (!isGameOver) {
                        update()
                        render(gc)
                    }
                    lastUpdate = now
                }
            }
        }

        timer.start()

        primaryStage.show()
    }

    private fun generateFood() {
        foodX = random.nextInt(width)
        foodY = random.nextInt(height)
    }

    private fun update() {
        val head = snake.first
        val newHead = when (direction) {
            Direction.UP -> Pair(head.first, head.second - 1)
            Direction.DOWN -> Pair(head.first, head.second + 1)
            Direction.LEFT -> Pair(head.first - 1, head.second)
            Direction.RIGHT -> Pair(head.first + 1, head.second)
        }
        snake.addFirst(newHead)

        if (newHead.first == foodX && newHead.second == foodY) {
            generateFood()
        } else {
            snake.removeLast()
        }

        if (newHead.first < 0 || newHead.first >= width || newHead.second < 0 || newHead.second >= height || newHead in snake.drop(1)) {
            isGameOver = true
        }
    }

    private fun render(gc: GraphicsContext) {
        gc.clearRect(0.0, 0.0, width * tileSize, height * tileSize)
        gc.fill = Color.GREEN
        snake.forEach { (x, y) ->
            gc.fillRect(x * tileSize, y * tileSize, tileSize, tileSize)
        }
        gc.fill = Color.RED
        gc.fillRect(foodX * tileSize, foodY * tileSize, tileSize, tileSize)

        if (isGameOver) {
            gc.fill = Color.BLACK
            gc.fillText("Game Over", (width * tileSize) / 2 - 40, (height * tileSize) / 2)
        }
    }
}

fun main() {
    Application.launch(HelloApplication::class.java)
}