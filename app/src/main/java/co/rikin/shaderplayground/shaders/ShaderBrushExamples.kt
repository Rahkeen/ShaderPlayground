package co.rikin.shaderplayground.shaders

import android.graphics.RuntimeShader
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.rikin.shaderplayground.SimpleSketchWithCache
import co.rikin.shaderplayground.ui.theme.DarkBlue
import co.rikin.shaderplayground.ui.theme.Lavender
import co.rikin.shaderplayground.ui.theme.ShaderPlaygroundTheme
import org.intellij.lang.annotations.Language

@Language("AGSL")
val Util = """
float pi = 3.1415926536;
float twopi = 6.28318530718;

float random(float2 st) {
    return fract(sin(dot(st.xy, float2(12.9898,78.233))) * 43758.5453123);
}

float plot (vec2 st, float pct){
    return smoothstep( pct-0.01, pct, st.y) - smoothstep( pct, pct+0.01, st.y);
}
""".trimIndent()

@Language("AGSL")
private val gradientShader = """
  uniform float2 resolution;
  uniform float time;
  
  layout(color) uniform half4 color1;
  layout(color) uniform half4 color2;
    
  $Util
  
  half4 main(float2 coord) {
      float2 uv = coord.xy / resolution;
      half3 color = half3(0.0);
      float3 pct = float3(smoothstep(0., 1., uv.x));
      
      float noise = random(uv);
      
//      pct.r = smoothstep(0.0, 1.0, uv.x);
      pct.g = sin(0.5 * pi * uv.x);
//      pct.b = pow(uv.x, 2.0);
      
      color = mix(color1.rgb, color2.rgb, pct);
      color = mix(color, float3(noise), 0.2);
//      color = color * (1.0 - float3(noise) * 0.3);
      
//      color = mix(color, half3(1.0, 0.0, 0.0), plot(uv, pct.r));
//      color = mix(color, half3(0.0, 1.0, 0.0), plot(uv, pct.g));
//      color = mix(color, half3(0.0, 0.0, 1.0), plot(uv, pct.b));
          
      return half4(color, 1.0);
  }
""".trimIndent()


@Preview
@Composable
fun RegularBox() {
  Canvas(
    modifier = Modifier
      .fillMaxWidth()
      .aspectRatio(1f)
  ) {
    drawRect(brush = Brush.horizontalGradient(colors = listOf(Lavender, DarkBlue)))
  }
}

@Preview
@Composable
fun ShaderBox() {
  val shader = remember { RuntimeShader(gradientShader) }
  with(shader) {
    setColorUniform("color1", Lavender.toArgb())
    setColorUniform("color2", DarkBlue.toArgb())
  }
  SimpleSketchWithCache(
    modifier = Modifier
      .fillMaxWidth()
      .aspectRatio(1f)
  ) { timeState ->
    with(shader) {
      setFloatUniform("resolution", size.width, size.height)
      setFloatUniform("time", timeState.value)
    }
    onDrawBehind {
      drawRect(ShaderBrush(shader))
    }
  }
}

@Preview
@Composable
fun GradientPlayground() {
  ShaderPlaygroundTheme {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .background(Color.White),
      contentAlignment = Alignment.Center
    ) {
      var buttonWidth by remember { mutableStateOf(0f) }
      var buttonHeight by remember { mutableStateOf(0f) }
      val buttonShader = remember { RuntimeShader(gradientShader) }.apply {
        setFloatUniform("resolution", buttonWidth, buttonHeight)
        setColorUniform("color1", Lavender.toArgb())
        setColorUniform("color2", DarkBlue.toArgb())
      }

      Box(
        modifier = Modifier
          .width(200.dp)
          .height(80.dp)
          .clip(CircleShape)
          .background(brush = ShaderBrush(buttonShader))
          .onSizeChanged { size ->
            buttonWidth = size.width.toFloat()
            buttonHeight = size.height.toFloat()
          },
        contentAlignment = Alignment.Center
      ) {
        Text("🌾", color = Color.White, fontSize = 20.sp)
      }
    }
  }
}

@Preview
@Composable
fun GrainyText() {
  ShaderPlaygroundTheme {
    var width by remember { mutableStateOf(0f) }
    var height by remember { mutableStateOf(0f) }

    val shader = remember {
      RuntimeShader(gradientShader).apply {
        setColorUniform("color1", Lavender.toArgb())
        setColorUniform("color2", DarkBlue.toArgb())
      }
    }

    shader.setFloatUniform(
      "resolution",
      width,
      height
    )

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f), contentAlignment = Alignment.Center
    ) {
    }
  }
}
