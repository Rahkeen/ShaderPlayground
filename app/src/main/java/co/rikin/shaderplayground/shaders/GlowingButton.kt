package co.rikin.shaderplayground.shaders

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.rikin.shaderplayground.SimpleSketchWithCache
import co.rikin.shaderplayground.ui.theme.DarkBlue
import co.rikin.shaderplayground.ui.theme.Juicy
import co.rikin.shaderplayground.ui.theme.JuicyLight
import co.rikin.shaderplayground.ui.theme.Lightsaber
import co.rikin.shaderplayground.ui.theme.LightsaberLight
import co.rikin.shaderplayground.ui.theme.SaberGradient
import co.rikin.shaderplayground.ui.theme.ShaderPlaygroundTheme
import org.intellij.lang.annotations.Language

@Language("AGSL")
val glowingButtonShader = """
    uniform shader button;
    uniform float2 size;
    uniform float radius;
    
    uniform float maxGlowAmount;
    uniform float glowCutoffDistance;
    
    uniform half4 glowColor;
    
    // this function helps determine if a point is in our rounded rectangle
    float roundedRectangleSDF(vec2 position, vec2 box, float radius) {
        vec2 q = abs(position) - box + radius;
        return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - radius;   
    }
    
    half4 main(float2 coord) {
        float2 rectangle = float2(size.x, size.y) / 2.0; 
        float distanceToClosestEdge = roundedRectangleSDF(
            coord - rectangle,
            rectangle,
            radius
        );
        half4 c = button.eval(coord);
        if(distanceToClosestEdge <= 0.0) {
            // we are in button
            return c;
        }
//        float clamped = min(distanceToClosestEdge / 2000.0, 1.0);
//        return half4(half3((coord - rectangle).x * (coord - rectangle).y), 1.0);
        
        // how much glow do we want
        float distanceFraction = max(1.0 - distanceToClosestEdge / glowCutoffDistance, 0.0);
        distanceFraction = pow(distanceFraction, 1.5);

        float glowAmount = maxGlowAmount * distanceFraction * glowColor.a;

        return half4(glowColor.r * glowAmount, glowColor.g * glowAmount, glowColor.b * glowAmount, glowAmount);
    }
""".trimIndent()

@Preview
@Composable
fun GlowingButton() {
  ShaderPlaygroundTheme {
    val shader = remember { RuntimeShader(glowingButtonShader) }
    var width by remember { mutableStateOf(0f) }
    var height by remember { mutableStateOf(0f) }

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val glow by animateFloatAsState(
      targetValue = if (pressed) 1.0f else 0f,
      animationSpec = tween(
        durationMillis = 500,
        easing = EaseInOut
      ),
      label = "glow"
    )

    shader.setFloatUniform("maxGlowAmount", glow);
    shader.setFloatUniform(
      "glowColor",
      Lightsaber.red,
      Lightsaber.green,
      Lightsaber.blue,
      Lightsaber.alpha,
    )
    shader.setFloatUniform("glowCutoffDistance", 200f);
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(color = Color.Black),
      contentAlignment = Alignment.Center
    ) {
      Box(
        modifier = Modifier
          .graphicsLayer {
//            clip = true
//            shape = object : Shape {
//              override fun createOutline(
//                size: Size,
//                layoutDirection: LayoutDirection,
//                density: Density
//              ): Outline {
//                return Outline.Rectangle(
//                  rect = Rect(-30f, -30f, size.width + 30f, size.height + 30f)
//                )
//              }
//            }
            with(shader) {
              setFloatUniform(
                "size",
                width,
                height
              )
              setFloatUniform(
                "radius",
                30.dp.toPx()
              )
            }
            renderEffect = RenderEffect
              .createRuntimeShaderEffect(shader, "button")
              .asComposeRenderEffect()

          }
          .width(100.dp)
          .height(60.dp)
          .background(color = Lightsaber, shape = RoundedCornerShape(30.dp))
          .clip(RoundedCornerShape(30.dp))
          .clickable(interactionSource = interactionSource, indication = null) {}
          .onSizeChanged { size ->
            width = size.width.toFloat()
            height = size.height.toFloat()
          },
        contentAlignment = Alignment.Center
      ) {
        Text("Glow", color = Color.White)
      }
    }
  }
}

@Language("AGSL")
val glowShader = """
  uniform float2 resolution;
  
  float getGlow(float dist, float radius, float intensity){
      return pow(radius/dist, intensity);
  }
  
  half4 main(float2 coord) {
      // normalize coords (0 -> 1) 0,0 is top left
      float2 uv = coord.xy /resolution;
      // center coord system (-.5 -> 0.5) 0,0 is center
      float2 pos = uv - 0.5;
      
      //********* Glow ***************************
      // Hyperbola given length of point from center, intense at 0 and quickly falls off 
      float dist = length(pos);
      
      // Radius: lower to make glow radius smaller
//      dist *= 0.1;
     
      // Intensity: Raising result to a power allows us to control fading
//      dist = pow(dist, 0.8);
        
      dist = getGlow(dist, 0.2, 1.0);
     
      // Color
      half3 color = dist * half3(0.1,0.4,1.0);
      
      color = 1.0 - exp(-color);
          
      return half4(color, 1.0);
  }
""".trimIndent()

@Preview
@Composable
fun GlowPlayground() {
  ShaderPlaygroundTheme {
    val shader = remember { RuntimeShader(glowShader) }
    SimpleSketchWithCache(modifier = Modifier
      .fillMaxWidth()
      .aspectRatio(1f)
    ) { time ->
      with(shader) {
        setFloatUniform(
          "resolution",
          size.width,
          size.height
        )
      }
      onDrawBehind {
        drawRect(brush = ShaderBrush(shader))
      }
    }
  }
}


@Language("AGSL")
val glowingButton2Shader = """
  uniform shader button;
  uniform float2 size;
  uniform float radius;
  
  uniform half4 glowColor;
  
  float roundRectSDF(vec2 position, vec2 box, float radius) {
      vec2 q = abs(position) - box + radius;
      return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - radius;   
  }
  
  float getGlow(float dist, float radius, float intensity){
      return pow(radius/dist, intensity);
  }
  
  half4 main(float2 coord) {
      float ratio = size.y / size.x;
      
      float2 normRect = float2(1.0, ratio);
      float2 normRectCenter = normRect - float2(0.5, 0.5 * ratio);
      float2 pos = coord.xy / size;
      pos.y = pos.y * ratio;
      pos = pos - normRectCenter;
      float normRadius = ratio / 2.0;
      float normDistance = roundRectSDF(pos, normRectCenter, normRadius);
      
      
      float2 rectangleCenter = size / 2.0;
      float2 adjustedCoord = coord - rectangleCenter;
      float distance = roundRectSDF(adjustedCoord, rectangleCenter, radius);
      
      half4 color = button.eval(coord);
      if (normDistance < 0.0) {
        return color;
      } 
      
      // Add some GLOW
      float glow = getGlow(normDistance, 0.3, 1.0);
      color = glow * glowColor;
      
      color = color * smoothstep(-0.5, 0.5, normDistance);
      
      // tonemapping
      color = 1.0 - exp(-color);
      
      return color;
  }
""".trimIndent()

@Preview
@Composable
fun GlowingButton2() {
  ShaderPlaygroundTheme {
    val shader = remember { RuntimeShader(glowingButton2Shader) }
    var width by remember { mutableStateOf(0f) }
    var height by remember { mutableStateOf(0f) }
    val color = remember { Lightsaber }

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    shader.setFloatUniform(
      "glowColor",
      color.red,
      color.green,
      color.blue,
      color.alpha,
    )

    Box(
      modifier = Modifier
        .fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      Box(
        modifier = Modifier
          .graphicsLayer {
            with(shader) {
              setFloatUniform(
                "size",
                width,
                height
              )
              setFloatUniform(
                "radius",
                30.dp.toPx()
              )
            }
            renderEffect = RenderEffect
              .createRuntimeShaderEffect(shader, "button")
              .asComposeRenderEffect()

          }
          .width(100.dp)
          .height(60.dp)
          .background(color = Color.White, shape = RoundedCornerShape(30.dp))
          .clip(RoundedCornerShape(30.dp))
          .clickable(interactionSource = interactionSource, indication = null) {}
          .onSizeChanged { size ->
            width = size.width.toFloat()
            height = size.height.toFloat()
          },
        contentAlignment = Alignment.Center
      ) {
        Text("Glow", color = Color.Black)
      }
    }
  }
}
