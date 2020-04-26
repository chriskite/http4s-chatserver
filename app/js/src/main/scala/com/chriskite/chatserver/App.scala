package com.chriskite.chatserver

import org.scalajs.dom
import org.scalajs.dom.raw._

object App  {
  val joinButton = dom.document.getElementById("join").asInstanceOf[HTMLInputElement]
  val sendButton = dom.document.getElementById("send").asInstanceOf[HTMLInputElement]

  def main(args: Array[String]): Unit = {
    val nameField = dom.document.getElementById("name").asInstanceOf[HTMLInputElement]
    joinButton.onclick = { (event: MouseEvent) =>
      joinChat(nameField.value)
      event.preventDefault()
    }
    nameField.focus()
    nameField.onkeypress = { (event: KeyboardEvent) =>
      if (event.keyCode == 13) {
        joinButton.click()
        event.preventDefault()
      }
    }
  }

  def joinChat(userName: String): Unit = {
    joinButton.disabled = true
    val chat = new WebSocket(getWebSocketUri(userName))
    val playground = dom.document.getElementById("playground")
    playground.innerHTML = s"Trying to join chat as '$userName'..."
    chat.onopen = { (event: Event) =>
      playground.insertBefore(p("Chat connection was successful!"), playground.firstChild)
      sendButton.disabled = false

      val messageField = dom.document.getElementById("message").asInstanceOf[HTMLInputElement]
      messageField.focus()
      messageField.onkeypress = { (event: KeyboardEvent) =>
        if (event.keyCode == 13) {
          sendButton.click()
          event.preventDefault()
        }
      }
      sendButton.onclick = { (event: Event) =>
        chat.send(messageField.value)
        messageField.value = ""
        messageField.focus()
        event.preventDefault()
      }

      event
    }
    chat.onerror = { (event: Event) =>
      playground.insertBefore(p(s"Failed: code: ${event.asInstanceOf[ErrorEvent].colno}"), playground.firstChild)
      joinButton.disabled = false
      sendButton.disabled = true
    }
    chat.onmessage = { (event: MessageEvent) =>
      writeToArea(event.data.toString)
    }
    chat.onclose = { (event: Event) =>
      playground.insertBefore(p("Connection to chat lost. You can try to rejoin manually."), playground.firstChild)
      joinButton.disabled = false
      sendButton.disabled = true
    }

    def writeToArea(text: String): Unit =
      playground.insertBefore(p(text), playground.firstChild)
  }


  private def getWebSocketUri(userName: String): String = {
    val wsProtocol = if (dom.document.location.protocol == "https:") "wss" else "ws"
    s"$wsProtocol://${dom.document.location.host}/ws/$userName"
  }

  def p(msg: String) = {
    val paragraph = dom.document.createElement("p")
    paragraph.innerHTML = msg
    paragraph
  }
}
