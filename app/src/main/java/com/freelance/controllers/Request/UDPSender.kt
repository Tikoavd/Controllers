package com.freelance.controllers.Request

import android.content.Context
import android.net.Uri
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.freelance.controllers.R
import java.io.IOException
import java.net.*


class UDPSender {
    val toastHandler = android.os.Handler(Looper.getMainLooper())
    fun SendTo(context: Context, uri: Uri) {
        var msg = Uri.decode(uri.lastPathSegment)
        var msgBytes = msg.toByteArray()
        if (msg.startsWith("\\0x")) {
            msg = msg.replace("\\0x", "0x")
            msgBytes = msg.toByteArray()
        } else if (msg.startsWith("0x")) {
            msg = msg.replace("0x", "")
            if (!msg.matches("[a-fA-F0-9]+".toRegex())) {
                Toast.makeText(context, "ERROR: Invalid hex values", Toast.LENGTH_LONG).show()
                return
            }
            msgBytes = msg.hexToBytes()
        }

        val buf = msgBytes
        val appName = context.getString(R.string.app_name)
        Log.d(appName, String(msgBytes))
        //Log.d(appName, "0x" + msgBytes.bytesToHex())

        Thread {
            try {
                val serverAddress = InetAddress.getByName(uri.host)
                //Log.v(getString(R.string.app_name), serverAddress.getHostAddress());
                val socket = DatagramSocket()
                if (!socket.broadcast) socket.broadcast = true
                val packet = DatagramPacket(buf, buf.size, serverAddress, uri.port)
                socket.send(packet)
                socket.close()
            } catch (e: UnknownHostException) {
                toastHandler.post {
                    Toast.makeText(
                        context, e.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                e.printStackTrace()
            } catch (e: SocketException) {
                toastHandler.post {
                    Toast.makeText(
                        context, e.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                e.printStackTrace()
            } catch (e: IOException) {
                toastHandler.post {
                    Toast.makeText(
                        context, e.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                e.printStackTrace()
            }
        }.start()
    }
}