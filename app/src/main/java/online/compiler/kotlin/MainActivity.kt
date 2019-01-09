package online.compiler.kotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {

    private val url = "https://try.kotlinlang.org/kotlinServer"
    private val serverDelimiter = "?"
    private val paramsDelimiter = "&"
    private val typeKeyValue = "type=run"
    private var projectKey = "project="
    private val filenameKeyValue = "filename=Simplest+version.kt"

    private val projectValuePart1 = "{\"id\":\"/Examples/Hello,%20world!/Simplest%20version\",\"name\":\"Simplest version\",\"args\":\"\",\"compilerVersion\":null,\"confType\":\"java\",\"originUrl\":\"/Examples/Hello,%20world!/Simplest%20version\",\"files\":[{\"name\":\"Simplest version.kt\",\"text\":\""
    //val source = "fun main(args: Array<String>) {\\n    println(\\\"Hello, world!\\\")\\n}"
    private val projectValuePart2 = "\",\"publicId\":\"/Examples/Hello,%20world!/Simplest%20version/Simplest%20version.kt\"}],\"readOnlyFileNames\":[]}"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText.setText("fun main(args: Array<String>) {\n" +
                "    println(\"Hello, world!\")\n" +
                "\n" +
                "}")

        button.setOnClickListener {
            launch  {
                //val projectKeyValue = "$projectValuePart1$source$projectValuePart2"//${editText.text}"
                //Log.d("1. SOURCE", URLEncoder.encode(source, "UTF-8"))
                //Log.d("2. SOURCE", URLEncoder.encode(editText.text.toString().replace("\n","\\n").replace("\"", "\\\""), "UTF-8"))
                //Log.d("3. SOURCE", URLEncoder.encode(editText.text.toString(), "UTF-8").replace("%0A", "%5Cn"))

                val projectKeyValue = "$projectValuePart1" +
                        "${editText.text.toString().replace("\n","\\n")
                            .replace("\"", "\\\"")}" +
                        "$projectValuePart2"
                val urlAddress = "$url$serverDelimiter" +
                        "$typeKeyValue$paramsDelimiter" +
                        "$projectKey${URLEncoder.encode(projectKeyValue, "UTF-8")}$paramsDelimiter" +
                        "$filenameKeyValue"
                val sb =  StringBuilder()
                try {
                    val url = URL(urlAddress)
                    val con = url.openConnection() as HttpURLConnection
                    val requestInputStream = con.run {
                        readTimeout = 10000 /* milliseconds */
                        connectTimeout = 20000 /* milliseconds */
                        requestMethod = "POST"
                        doInput = true
                        // Start the query
                        connect()
                        inputStream
                    }

                    val bufferReader = BufferedReader(InputStreamReader(requestInputStream), 4096)
                    var line: String?

                    line = bufferReader.readLine()
                    while (line != null) {
                        sb.append(line)
                        line = bufferReader.readLine()
                    }
                    bufferReader.close()
                } catch (e: IOException) {
                    //handle the exception !
                    sb.append(e.message ?: "-1")
                }
                //val result = URL(url).readText()
                runOnUiThread() {
                    textView.text = "Response: $sb"
                    Log.d("Response", sb.toString())
                    Toast.makeText(applicationContext, "Request performed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
