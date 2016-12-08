/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.vertx.scala.core.eventbus

import io.vertx.lang.scala.HandlerOps._
import io.vertx.lang.scala.Converter._
import scala.reflect.runtime.universe._
import scala.compat.java8.FunctionConverters._
import scala.collection.JavaConverters._
import io.vertx.core.eventbus.{MessageConsumer => JMessageConsumer}
import io.vertx.core.streams.{ReadStream => JReadStream}
import io.vertx.scala.core.streams.ReadStream
import io.vertx.core.eventbus.{Message => JMessage}

/**
  * An event bus consumer object representing a stream of message to an [[io.vertx.scala.core.eventbus.EventBus]] address that can
  * be read from.
  * 
  * The [[io.vertx.scala.core.eventbus.EventBus#consumer]] or [[io.vertx.scala.core.eventbus.EventBus#localConsumer]]
  * creates a new consumer, the returned consumer is not yet registered against the event bus. Registration
  * is effective after the [[io.vertx.scala.core.eventbus.MessageConsumer#handler]] method is invoked.
  *
  * The consumer is unregistered from the event bus using the [[io.vertx.scala.core.eventbus.MessageConsumer#unregister]] method or by calling the
  * [[io.vertx.scala.core.eventbus.MessageConsumer#handler]] with a null value..
  */
class MessageConsumer[T: TypeTag](private val _asJava: JMessageConsumer[Object]) 
    extends ReadStream[Message[T]] {

  def asJava: JMessageConsumer[Object] = _asJava

  def exceptionHandler(handler: io.vertx.core.Handler[Throwable]): MessageConsumer[T] = {
    _asJava.exceptionHandler(funcToMappedHandler[java.lang.Throwable, Throwable](x => toScala(x))(handler).asInstanceOf)
    this
  }

  def handler(handler: io.vertx.core.Handler[Message[T]]): MessageConsumer[T] = {
    _asJava.handler(funcToMappedHandler(Message.apply[T])(handler).asInstanceOf)
    this
  }

  def pause(): MessageConsumer[T] = {
    _asJava.pause()
    this
  }

  def resume(): MessageConsumer[T] = {
    _asJava.resume()
    this
  }

  def endHandler(endHandler: io.vertx.core.Handler[Unit]): MessageConsumer[T] = {
    _asJava.endHandler(funcToMappedHandler[java.lang.Void, Unit](x => toScala(x))(_ => endHandler.handle()).asInstanceOf)
    this
  }

  /**
    * @return a read stream for the body of the message stream.
    */
  def bodyStream(): ReadStream[T] = {
    ReadStream.apply[T](_asJava.bodyStream())
  }

  /**
    * @return true if the current consumer is registered
    */
  def isRegistered(): Boolean = {
    _asJava.isRegistered()
  }

  /**
    * @return The address the handler was registered with.
    */
  def address(): String = {
    _asJava.address()
  }

  /**
    * Set the number of messages this registration will buffer when this stream is paused. The default
    * value is <code>0</code>. When a new value is set, buffered messages may be discarded to reach
    * the new value.
    * @param maxBufferedMessages the maximum number of messages that can be buffered
    * @return this registration
    */
  def setMaxBufferedMessages(maxBufferedMessages: Int): MessageConsumer[T] = {
    MessageConsumer.apply[T](_asJava.setMaxBufferedMessages(maxBufferedMessages))
  }

  /**
    * @return the maximum number of messages that can be buffered when this stream is paused
    */
  def getMaxBufferedMessages(): Int = {
    _asJava.getMaxBufferedMessages()
  }

  /**
    * Optional method which can be called to indicate when the registration has been propagated across the cluster.
    * @return the completion future
    */
  def completionFuture(): concurrent.Future[Unit] = {
    val promiseAndHandler = handlerForAsyncResultWithConversion[Unit]((x => ()))
    _asJava.completionHandler(promiseAndHandler._1.asInstanceOf)
    promiseAndHandler._2.future
  }

  /**
    * Unregisters the handler which created this registration
    */
  def unregister(): Unit = {
    _asJava.unregister()
  }

  /**
    * Unregisters the handler which created this registration
    * @return the future called when the unregister is done. For example in a cluster when all nodes of the event bus have been unregistered.
    */
  def unregisterFuture(): concurrent.Future[Unit] = {
    val promiseAndHandler = handlerForAsyncResultWithConversion[Unit]((x => ()))
    _asJava.unregister(promiseAndHandler._1.asInstanceOf)
    promiseAndHandler._2.future
  }

}

object MessageConsumer {

  def apply[T: TypeTag](_asJava: JMessageConsumer[Object]): MessageConsumer[T] =
    new MessageConsumer(_asJava)

}
