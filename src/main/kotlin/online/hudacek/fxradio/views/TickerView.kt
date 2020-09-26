package online.hudacek.fxradio.views

import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.ContextMenu
import javafx.scene.layout.Pane
import javafx.scene.shape.Rectangle
import javafx.util.Duration
import online.hudacek.fxradio.extension.copyMenu
import online.hudacek.fxradio.extension.openUrl
import online.hudacek.fxradio.extension.update
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import tornadofx.*
import java.util.concurrent.ConcurrentLinkedQueue

//source:
//https://gitlab.light.kow.is/kowis-projects/deskscreen/-/blob/marquee/src/main/kotlin/is/kow/deskscreen/ticker/TickerView.kt
open class TickerEntry<T : Node>(
        var content: T,
        var reschedule: Boolean = false
) {
    open fun updateObservable(): Completable? = null
}

/**
 * Display a ticker view of things
 * I want the ticker to always run, and things can be injected to be the *next* thing to display
 */
class TickerView : View() {

    private val marqueeView: MarqueeView by inject()
    private var copyMenu: ContextMenu by singleAssign()

    private val playerViewModel: PlayerViewModel by inject()

    private val youtubeSearchUrl = "https://www.youtube.com/results?search_query="

    init {
        playerViewModel.stationProperty.onChange {
            if (it != null) {
                copyMenu.apply {
                    items[1].apply {
                        isDisable = false
                        action {
                            it.url_resolved?.let { url -> clipboard.update(url) }
                        }
                    }
                }
            }
        }
    }

    override val root = pane {
        prefHeight = 15.0
        marqueeView.inside(this)
        copyMenu = copyMenu(clipboard, name = messages["copy"]) {
            items[0].apply {
                isDisable = true
            }
            item(messages["copy.stream.url"]) {
                isDisable = true
            }
            item(messages["search.on.youtube"]) {
                isDisable = true
            }
        }
        add(marqueeView)
    }

    fun updateText(text: String) {
        copyMenu.apply {
            items[0].apply {
                isDisable = false
                action {
                    clipboard.update(text)
                }
            }
            items[2].apply {
                isDisable = false
                action {
                    app.openUrl(youtubeSearchUrl, text)
                }
            }
        }
        marqueeView.clear()
        marqueeView.enqueueTickEntry(TickerEntry(content = createText(text), reschedule = true))
    }

    private fun createText(content: String) = text(content) {
        layoutY = 12.0
    }
}

class MarqueeView : View() {

    private val offset = 10.0 //Amount of space between entries!

    private data class ActiveTick(val entry: TickerEntry<Node>, var cleared: Boolean = false)

    private val activeTicks = ConcurrentLinkedQueue<ActiveTick>() //This might not need to be threadsafe, only one thing is adding/removing it
    private val queuedTicks = ConcurrentLinkedQueue<TickerEntry<Node>>() //This one does, multiple threads!

    private val timeline = Timeline() //Timeline is up here in case I need to pause, play the animation

    override val root = pane()

    //This is needed to make sure the pane fills up the entire space of whatever we've been put in.
    fun inside(of: Pane) {
        //I need this guy to autofill to max size
        root.prefWidthProperty().bind(of.widthProperty())
        root.prefHeightProperty().bind(of.heightProperty())
    }

    init {
        val rectangle = Rectangle(25.0, 25.0)

        //Bind the clipping to the size of the thing
        rectangle.widthProperty().bind(root.widthProperty())
        rectangle.heightProperty().bind(root.heightProperty())
        root.clip = rectangle

        startAnimation() //Fire up the animation process
    }

    fun enqueueTickEntry(entry: TickerEntry<Node>) = queuedTicks.add(entry)

    //Fire up the animation process for the ticker
    private fun startAnimation() {
        //If it's already cleared, don't keep checking
        fun lastOneCleared(): Boolean {
            //Determine if the last entry in the activeQueue has cleared
            val last = activeTicks.last()
            if (!last.cleared) {
                //Only do the math one time, well up to many times, but it might be cleared, and then clean it out
                val entry = last.entry
                if (entry.content.layoutBounds.width + entry.content.layoutX + offset <= root.width) {
                    last.cleared = true
                }
            }
            return last.cleared
        }

        //Keep track of the things we've started, so we can dispose them
        val subscriptions = HashMap<TickerEntry<Node>, Disposable>()

        //Could also update this things speed time so that stuff scrolls faster
        val updateFrame = KeyFrame(Duration.millis(35.0), EventHandler {

            //There's possible some thread bugs in there, but only on the very first creation
            if (activeTicks.isEmpty() || lastOneCleared()) {
                val newTickerEntry: TickerEntry<Node>? = queuedTicks.poll()
                if (newTickerEntry != null) {
                    //Then just put it into the active queue, so it will start processing like normal
                    newTickerEntry.content.layoutX = root.prefWidth //Where to start
                    activeTicks.add(ActiveTick(newTickerEntry))
                    root.add(newTickerEntry.content) //this is where it gets added
                }
            }

            activeTicks.forEach { active ->
                val entry = active.entry
                val content = entry.content
                val textWidth = content.layoutBounds.width
                val layoutX = content.layoutX

                //Check to see if it's been animated out.
                if (layoutX <= 0 - textWidth - (2 * offset)) {
                    //Now I ned to figure out how to remove it
                    entry.content.removeFromParent() //Is this legit?
                    activeTicks.remove(active) //no longer here, shouldn't ruin the loop
                    if (subscriptions.containsKey(entry)) {
                        subscriptions.remove(entry)!!.dispose() //This should cancel it
                    }
                    if (entry.reschedule) {
                        //just stick it back in the queue
                        enqueueTickEntry(entry)
                    }
                } else {
                    //It's not moved out, so move it a pixel. We could move it some number of pixels
                    content.layoutX = content.layoutX - 1

                    //If there's an observable that we haven't started, fire it up!
                    val updateObservable = entry.updateObservable()
                    if (updateObservable != null && !subscriptions.containsKey(entry)) {
                        //Start up the observable that updates the component, whatever it is
                        val disposable = updateObservable.subscribe()
                        subscriptions[entry] = disposable
                    }
                }
            }
        })

        timeline.keyFrames.add(updateFrame)
        timeline.cycleCount = Animation.INDEFINITE
        timeline.play()
    }


    fun clear() {
        activeTicks.clear()
        queuedTicks.clear()
        root.clear()
    }
}