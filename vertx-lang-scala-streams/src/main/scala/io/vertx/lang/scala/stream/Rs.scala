package io.vertx.lang.scala.stream

import io.vertx.lang.scala.stream.api.{FailureStrategy, Sink}
import io.vertx.lang.scala.stream.builder.StreamBuilder
import io.vertx.lang.scala.stream.failurestrategy.SkipStrategy
import io.vertx.lang.scala.stream.sink.WriteStreamSink
import io.vertx.lang.scala.stream.source.ReadStreamSource
import io.vertx.lang.scala.stream.stage._
import io.vertx.scala.core.streams.{ReadStream, WriteStream}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by jochen on 01.05.17.
  */
object Rs {

  implicit class ReadStreamBuilder[O](val rs: ReadStream[O]) {
    def toSource(failureStrategy:FailureStrategy = SkipStrategy()): StreamBuilder[O] = StreamBuilder(() => new ReadStreamSource(rs), failureStrategy)
  }

  implicit class WriteStreamBuilder[I](val ws: WriteStream[I]) {
    def toSink(): WriteStreamSink[I] = new WriteStreamSink[I](ws)
  }

  implicit class BuilderExtender[I](val builder:StreamBuilder[I]) {
    def map[O](f: I => O):StreamBuilder[O] = builder.next(() => new MapStage(f))

    def filter[O](f: I => Boolean):StreamBuilder[I] = builder.next(() => new FilterStage[I](f))

    def process(f: I => Unit):StreamBuilder[I] = builder.next(() => new ProcessStage(f))

    def future[O](f: I => Future[O])(implicit ec:ExecutionContext):StreamBuilder[O] = builder.next(() => new FutureStage(f, ec))

    def switchEc(ec: ExecutionContext):StreamBuilder[I] = builder.next(() => new SwitchExecutionContextStage(ec))

    def sink[O](sink: Sink[O]): StreamBuilder[O] = builder.next(() => sink).start()
  }
}