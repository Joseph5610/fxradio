package online.hudacek.fxradio.util

import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.rxjavafx.observables.JavaFxObservable
import io.reactivex.rxjavafx.observers.JavaFxObserver
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import javafx.beans.binding.Binding
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.control.ContextMenu
import javafx.scene.control.Dialog
import javafx.scene.control.MenuItem
import java.util.*

/**
 * Observes the emissions on the JavaFX Thread.
 * This is the same as calling Observable#observeOn(JavaFxScheduler.platform())
 */
fun <T> Observable<T>.observeOnFx() = observeOn(JavaFxScheduler.platform())

/**
 * Observes the emissions on the JavaFX Thread.
 * This is the same as calling Flowable#observeOn(JavaFxScheduler.platform())
 */
fun <T> Flowable<T>.observeOnFx() = observeOn(JavaFxScheduler.platform())


/**
 * Observes the emissions on the JavaFX Thread.
 * This is the same as calling Single#observeOn(JavaFxScheduler.platform())
 */
fun <T> Single<T>.observeOnFx() = observeOn(JavaFxScheduler.platform())

/**
 * Observes the emissions on the JavaFX Thread.
 * This is the same as calling Maybe#observeOn(JavaFxScheduler.platform())
 */
fun <T> Maybe<T>.observeOnFx() = observeOn(JavaFxScheduler.platform())

/**
 * Observes the emissions on the JavaFX Thread.
 * This is the same as calling Completable#observeOn(JavaFxScheduler.platform())
 */
fun Completable.observeOnFx() = observeOn(JavaFxScheduler.platform())


/**
 * Instructs the source Observable to emit items on the JavaFX Thread.
 * This is the same as calling Observable#subscribeOn(JavaFxScheduler.platform())
 */
fun <T> Observable<T>.subscribeOnFx() = subscribeOn(JavaFxScheduler.platform())

/**
 * Instructs the source Flowable to emit items on the JavaFX Thread.
 * This is the same as calling Flowable#subscribeOn(JavaFxScheduler.platform())
 */
fun <T> Flowable<T>.subscribeOnFx() = subscribeOn(JavaFxScheduler.platform())

/**
 * Observes the emissions on the JavaFX Thread.
 * This is the same as calling Single#subscribeOnFx(JavaFxScheduler.platform())
 */
fun <T> Single<T>.subscribeOnFx() = subscribeOn(JavaFxScheduler.platform())

/**
 * Observes the emissions on the JavaFX Thread.
 * This is the same as calling Maybe#subscribeOnFx(JavaFxScheduler.platform())
 */
fun <T> Maybe<T>.subscribeOnFx() = subscribeOn(JavaFxScheduler.platform())

/**
 * Observes the emissions on the JavaFX Thread.
 * This is the same as calling Completable#subscribeOnFx(JavaFxScheduler.platform())
 */
fun Completable.subscribeOnFx() = subscribeOn(JavaFxScheduler.platform())

/**
 * Create an rx Observable from a javafx ObservableValue
 * @param <T>          the type of the observed value
 * @return an Observable emitting values as the wrapped ObservableValue changes
 */
fun <T> ObservableValue<T>.toObservable() = JavaFxObservable.valuesOf(this)

/**
 * Create an rx Observable from a javafx ObservableValue
 * @param <T>          the type of the observed value
 * @param nullSentinel the default sentinel value emitted when the observable is null
 * @return an Observable emitting values as the wrapped ObservableValue changes
 */
fun <T> ObservableValue<T>.toObservable(nullSentinel: T) = JavaFxObservable.valuesOf(this, nullSentinel)


/**
 * Create an rx Observable from a javafx ObservableValue, emitting nullable values as Java 8 `Optional` types
 * @param <T>          the type of the observed value
 * @return an Observable emitting `Optional<T>` values as the wrapped ObservableValue changes
 */
fun <T> ObservableValue<T>.toNullableObservable() = JavaFxObservable.nullableValuesOf(this)

/**
 * Create an rx Observable from a javafx ObservableValue, and emits changes with old and new value pairs
 * @param <T>          the type of the observed value
 * @return an Observable emitting values as the wrapped ObservableValue changes
 */
fun <T> ObservableValue<T>.toObservableChanges() = JavaFxObservable.changesOf(this)

/**
 * Create an rx Observable from a javafx ObservableValue, and emits changes with old and new non-null value pairs
 * @param <T>          the type of the observed value
 * @return an Observable emitting non-null values as the wrapped ObservableValue changes
 */
fun <T> ObservableValue<T>.toObservableChangesNonNull() = JavaFxObservable.nonNullChangesOf(this)

/**
 * Creates an observable corresponding to javafx ContextMenu action events.
 * @return An Observable of UI ActionEvents
 */
fun ContextMenu.actionEvents() = JavaFxObservable.actionEventsOf(this)

/**
 * Creates an observable corresponding to javafx MenuItem action events.
 *
 * @param menuItem      The target of the ActionEvents
 * @return An Observable of UI ActionEvents
 */
fun MenuItem.actionEvents() = JavaFxObservable.actionEventsOf(this)

/**
 * Creates an observable corresponding to javafx Node action events.
 * @return An Observable of UI ActionEvents
 */
fun Node.actionEvents() = JavaFxObservable.actionEventsOf(this)

/**
 * Emits the response `T` for a given `Dialog<T>`. If no response is provided the Maybe  will be empty.
 */
fun <T> Dialog<T>.toMaybe() = JavaFxObservable.fromDialog(this)!!

/**
 * Turns an Observable into a JavaFX Binding. Calling the Binding's dispose() method will handle the disposal.
 */
fun <T> Observable<T>.toBinding(actionOp: (ObservableBindingSideEffects<T>.() -> Unit)? = null): Binding<T> {
    val transformer = actionOp?.let {
        val sideEffects = ObservableBindingSideEffects<T>()
        it.invoke(sideEffects)
        sideEffects.transformer
    }
    return JavaFxObserver.toBinding((transformer?.let { this.compose(it) } ?: this))
}

class ObservableBindingSideEffects<T> {
    private var onNextAction: ((T) -> Unit)? = null
    private var onCompleteAction: (() -> Unit)? = null
    private var onErrorAction: ((ex: Throwable) -> Unit)? = null

    fun onNext(onNext: (T) -> Unit): Unit {
        onNextAction = onNext
    }

    fun onComplete(onComplete: () -> Unit): Unit {
        onCompleteAction = onComplete
    }

    fun onError(onError: (ex: Throwable) -> Unit): Unit {
        onErrorAction = onError
    }

    internal val transformer: ObservableTransformer<T, T>
        get() = ObservableTransformer<T, T> { obs ->
            var withActions: Observable<T> = obs
            withActions = onNextAction?.let { withActions.doOnNext(onNextAction) } ?: withActions
            withActions = onCompleteAction?.let { withActions.doOnComplete(onCompleteAction) } ?: withActions
            withActions = onErrorAction?.let { withActions.doOnError(onErrorAction) } ?: withActions
            withActions
        }
}