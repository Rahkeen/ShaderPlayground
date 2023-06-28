package co.rikin.shaderplayground.shaders

import android.graphics.RuntimeShader
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.rikin.shaderplayground.SimpleSketchWithCache
import co.rikin.shaderplayground.ui.theme.ShaderPlaygroundTheme
import org.intellij.lang.annotations.Language

@Language("AGSL")
private val snowShader = """
  uniform float2 resolution;
  uniform float time;
  
  float rnd(float x) {
      return fract(sin(dot(float2(x+47.49,38.2467/(x+2.3)), float2(12.9898, 78.233)))* (43758.5453));
  }
  
  float drawCircle(float2 p, float2 center, float radius)
  {
      return 1.0 - smoothstep(0.0, radius, length(p - center));
  }
  
  half4 main(float2 coord) {
      float2 uv = coord.xy / resolution;
      uv.y = 1.0 - uv.y;
      half4 color = half4(0.808, 0.89, 0.918, 1.0);
      float j = 0.0;
      
      for(int i = 0; i < 100; i++) {
          j = float(i);
          float speed = 0.3+rnd(cos(j))*(0.7+0.5*cos(j/(float(200)*0.25)));
          float2 center = float2((0.25-uv.y)*0.2+rnd(j)+0.1*cos(time+sin(j)), mod(sin(j)-speed*(time*1.5*(0.1+0.2)), 0.65));
          color += half4(0.29*drawCircle(uv, center, 0.001+speed*0.004));
      }
          
      return color;
  }
""".trimIndent()

@Composable
fun SnowPlayground() {
  ShaderPlaygroundTheme {
    val shader = remember { RuntimeShader(snowShader) }
    SimpleSketchWithCache(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
    ) { time ->
      with(shader) {
        setFloatUniform(
          "resolution",
          size.width,
          size.height
        )

        setFloatUniform(
          "time",
          time.value
        )
      }

      onDrawBehind {
        drawRect(
          brush = ShaderBrush(shader)
        )
      }
    }
  }
}

@Language("AGSL")
private val swedenSnowShader = """
  uniform float2 resolution;
  uniform float time;
  
  half4 main(float2 coord) {
    float snow = 0.0;
    float ratio = resolution.y / resolution.x;
    float random = fract(sin(dot(coord.xy,vec2(12.9898,78.233)))* 43758.5453);
    for(int k=0;k<6;k++){
        for(int i=0;i<12;i++){
            float cellSize = 2.0 + (float(i)*3.0);
            float downSpeed = 0.3+(sin(time*0.4+float(k+i*20))+1.0)*0.00008;
            vec2 uv = vec2(coord.x/resolution.x, (1.0 - coord.y/resolution.y) * ratio)+vec2(0.01*sin((time+float(k*6185))*0.6+float(i))*(5.0/float(i)),downSpeed*(time+float(k*1352))*(1.0/float(i)));
            vec2 uvStep = (ceil((uv)*cellSize-vec2(0.5,0.5))/cellSize);
            float x = fract(sin(dot(uvStep.xy,vec2(12.9898+float(k)*12.0,78.233+float(k)*315.156)))* 43758.5453+float(k)*12.0)-0.5;
            float y = fract(sin(dot(uvStep.xy,vec2(62.2364+float(k)*23.0,94.674+float(k)*95.0)))* 62159.8432+float(k)*12.0)-0.5;

            float randomMagnitude1 = sin(time*2.5)*0.7/cellSize;
            float randomMagnitude2 = cos(time*2.5)*0.7/cellSize;

            float d = 5.0*distance((uvStep.xy + vec2(x*sin(y),y)*randomMagnitude1 + vec2(y,x)*randomMagnitude2),uv.xy);

            float omiVal = fract(sin(dot(uvStep.xy,vec2(32.4691,94.615)))* 31572.1684);
            if(omiVal<0.08?true:false){
                float newd = (x+1.0)*0.4*clamp(1.9-d*(15.0+(x*6.3))*(cellSize/1.4),0.0,1.0);
                snow += newd;
            }
        }
    }
    
    
    return half4(snow) + half4(0.0784,0.1294,0.2392,1.0) + random*0.01;
  }
""".trimIndent()

@Composable
fun SwedenSnowPlayground() {
  ShaderPlaygroundTheme {
    val shader = remember { RuntimeShader(swedenSnowShader) }

    SimpleSketchWithCache(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
    ) { time ->
      with(shader) {
        setFloatUniform(
          "resolution",
          size.width,
          size.height
        )

        setFloatUniform(
          "time",
          time.value
        )
      }

      onDrawBehind {
        drawRect(
          brush = ShaderBrush(shader)
        )
      }
    }
  }
}

@Preview
@Composable
fun SnowyButton() {
  ShaderPlaygroundTheme {
    val shader = remember { RuntimeShader(swedenSnowShader) }
    var time by remember { mutableStateOf(0f) }
    var width by remember { mutableStateOf(0f) }
    var height by remember { mutableStateOf(0f) }

    with(shader) {
      setFloatUniform("time", time)
      setFloatUniform("resolution", width, height)
    }

    LaunchedEffect(Unit) {
      do {
        withFrameMillis {
          time += 0.01f
        }
      } while (true)
    }

    Box(
      modifier = Modifier
        .width(200.dp)
        .height(80.dp)
        .clip(CircleShape)
        .background(brush = ShaderBrush(shader))
        .onSizeChanged { size ->
          width = size.width.toFloat()
          height = size.height.toFloat()
        },
      contentAlignment = Alignment.Center
    ) {
      Text(text = "Hello", color = Color.White, fontSize = 24.sp)
    }
  }
}
