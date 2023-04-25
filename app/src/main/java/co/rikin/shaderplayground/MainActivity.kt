package co.rikin.shaderplayground

import android.graphics.RuntimeShader
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.forEachGesture
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
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import co.rikin.shaderplayground.ui.theme.ShaderPlaygroundTheme

const val ShaderCode = """
uniform shader composable;
uniform float2 iResolution;
uniform float iTime;
uniform float2 iPointer;

half4 gradient(float2 fragCoord) {
    float2 uv = fragCoord.xy / iResolution;
    return half4(uv.x, uv.y, 0.0, 1.0);
}

half4 pointer(float2 fragCoord) {
    float2 normPointer = iPointer / iResolution;
    float2 center = normPointer;
    return half4(center.x, center.y, 0.0, 1.0);
}

float plot(float2 st) {
    return smoothstep(0.02, 0.0, abs(st.y - st.x));
}

float plot(float2 uv, float on) {
    return smoothstep(on-0.01, on, uv.y) - smoothstep(on, on+0.01, uv.y);
}

half4 shapingPractice(float2 fragCoord) {
    float pi = 3.1415926;
    float2 uv = fragCoord.xy/iResolution;
    //x -> (0, 1) to (-1, 1)
    //y -> (0, 1) to (0, 1)
    float slope = (1.0 - -1.0) / (1 - 0); // 2.0
    uv.x = -1.0 + slope * uv.x;
    uv.y = 1 - uv.y;
    // function for plotting x on the y axis
    float y = 1 - pow(abs(sin(pi * uv.x / 2.0)), 0.5); 
    float3 color = float3(y);
    
    float pct = plot(uv, y);
    // plot returns 1.0 when point is on the line, 0 otherwise, so it draws a green line
    color = (1.0-pct)*color+pct*float3(0.0,1.0,0.0);

	  return half4(color, 1.0);
}

float3 colorA = vec3(0.83529, 0.77647, 0.87843); // yellow ish
float3 colorB = vec3(0.09804, 0.16471, 0.31765); // blue ish

float random(float2 st) {
    return fract(sin(dot(st.xy, float2(12.9898,78.233))) * 43758.5453123);
}

half4 colorPractice(float2 fragCoord) {
    float2 uv = fragCoord.xy / iResolution;
    float3 color = float3(0.0);
    float noise = random(uv);
    float noiseX = uv.x + noise * 0.4;
    
    float3 pct = float3(noiseX);
    color = mix(colorA, colorB, pct);
    
    return half4(color, 1.0);
}

half4 main(float2 fragCoord) {
    return colorPractice(fragCoord);
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
              .pointerInput(Unit) {
                forEachGesture {
                  awaitPointerEventScope {
                    do {
                      val event = awaitPointerEvent()
                      val x = event.changes.last().position.x
                      val y = event.changes.last().position.y
                      shader.setFloatUniform(
                        "iPointer",
                        x,
                        y
                      )
                    } while (event.changes.none() { it.changedToUp() })
                  }
                }
              }
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
