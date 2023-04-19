package co.rikin.shaderplayground

import android.graphics.RuntimeShader
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.DrawResult
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import co.rikin.shaderplayground.ui.theme.ShaderPlaygroundTheme

const val ShaderCode = """
uniform shader composable;
uniform float2 iResolution;
uniform float iTime;
half4 main(float2 fragCoord) {
    return half4(abs(sin(iTime)), 0.0, 0.0, 1.0);
}
"""
val shader = RuntimeShader(ShaderCode)

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    WindowCompat.setDecorFitsSystemWindows(window, false)


    setContent {
      ShaderPlaygroundTheme {
        Column(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          SimpleSketchWithCache(
            modifier = Modifier
              .fillMaxWidth()
              .height(300.dp)
          ) { time ->
            onDrawBehind {
              shader.setFloatUniform(
                "iResolution",
                size.width,
                size.height
              )
              shader.setFloatUniform(
                "iTime",
                time.value
              )
              drawRect(
                brush = ShaderBrush(
                  shader = shader,
                )
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun SimpleSketchWithCache(
  modifier: Modifier = Modifier,
  speed: Float = 0.01f,
  onBuildDrawCache: CacheDrawScope.(time: State<Float>) -> DrawResult
) {
  val time = remember { mutableStateOf(0f) }

  LaunchedEffect(Unit) {
    do {
      withFrameMillis {
        time.value = time.value + speed
      }
    } while (true)
  }

  Box(modifier = modifier.drawWithCache { onBuildDrawCache(time) })
}
