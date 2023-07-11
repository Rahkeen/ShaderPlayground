package co.rikin.shaderplayground.book

import android.graphics.RuntimeShader
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.tooling.preview.Preview
import co.rikin.shaderplayground.SimpleSketchWithCache
import org.intellij.lang.annotations.Language

@Composable
fun rememberShader(@Language("AGSL") shader: String): RuntimeShader {
  return remember {
    RuntimeShader(shader)
  }
}

@Language("AGSL")
private val roundRectShader = """
uniform float2 resolution;
uniform float time;

float twopi = 6.28318530718;
half3 palette(float t) {
    half3 a = half3(0.5, 0.5, 0.5);
    half3 b = half3(0.5, 0.5, 0.5);
    half3 c = half3(1.0, 1.0, 1.0);
    half3 d = half3(0.00, 0.33, 0.67);
    
    return a + b * cos(twopi * (c * t + d));
}

half4 main(float2 coord) {
    // normalize coords
    float2 uv = coord.xy / resolution;
    uv = uv * 2.0 - 1.0;
    half3 color = half3(0.0);
    float d = 0.0;
    
    // create some animation helpers
    float animate = time * 0.4;
    float oscilate = abs(sin(animate));
    
    // this offset helps create the rectangle
    float offset = 0.2 * oscilate;
    float x = max(abs(uv.x) - offset, 0.0);
    float y = max(abs(uv.y) - offset, 0.0);
    // distance formula
    d = sqrt(x * x + y * y);
    
    float multiple = 20.0 * oscilate; 
    color = half3(fract(d * multiple));
    color = color * palette(d * oscilate);
        
    return half4(color, 1.0);
}
""".trimIndent()

@Language("AGSL")
val roundedRectShader = """
uniform float2 resolution;
uniform float time;

float twopi = 6.28318530718;

// A rainbow color scheme for our drawing
half3 palette(float t) {
      half3 a = half3(0.5, 0.5, 0.5);
      half3 b = half3(0.5, 0.5, 0.5);
      half3 c = half3(1.0, 1.0, 1.0);
      half3 d = half3(0.00, 0.33, 0.67);
      
      return a + b * cos(twopi * (c * t + d));
}

half4 main(float2 coord) {
    // normalize our coords, so that they range from 0..1
    float2 uv = coord.xy / resolution;
    half3 color = half3(0.0);
    float d = 0.0;
    float radius = 0.0;
    
    // ----------- PART 01 - DRAWING A CIRCLE -------------------
    
    // Modify the coordinate space by center the origin and expanding the range to -1..1
    uv = uv * 2.0 - 1.0;
    
    // Now our points are relative to the center, we can visual a distance field by
    // calculating the distance using our length() function. Setting the color to that
    // distance value shows us a radial gradient. Points closer to the center are darker.
    d = length(uv);
    
    // uncomment this line to see the result
    //color = half3(d);
    
    // We now have everything we really need to draw a circle. Let's decide on a radius and then
    // use our handy dandy step function to decide if a pixel is inside(black) or outside(white)
    // our circle. step() has a threshold value (our radius) and the input value (our distance). If
    // the value is less than the threshold step returns 0, else if it's greater it returns 1. 
    radius = 0.5;
    
    // uncomment this line to see the result
    //color = half3(step(radius, d));
    
    // We can make a cool repeating pattern effect using the fract() function. This just takes a float
    // and returns the fractional portion of it (so fract(1.234) -> .234). This is great because this
    // clamps values between 0 and 1. We can multiple d by some number and take the fract of that to get
    // a cool repeating pattern for the circular distance field. Feel free to play around with the multiplier
    // and see how it effects the output.
    
    // uncomment this line to see the result
    //color = half3(fract(d * 12));
    
    // Let's make it look more interesting by adding some color, i'm using a color palette from
    // https://iquilezles.org/articles/palettes/
    // I just extracted the code into a function based on the palette I wanted.
    
    // uncomment this line to see the result
    //color = color * palette(d);
    
    // ----------- PART 02 - ROUNDING RECTS -------------------
    
    // We now have all the foundational parts, we can create a round rect using some tricks
    // We'll start by shifting our center for our circle, and mirroring it across the x and y
    // axis using abs(uv). This creates 4 regions that range from 0..1, which we can then 
    // re-orient using our new center offset for each region.
    float center = 0.2;
    radius = 0.2;
    d = length(abs(uv)-center);
    
    // uncomment this line to see the result
    color = half3(fract(d * 12)) * palette(d);
    
    // It's almost what we want, we just need to merge these in a way that keeps the rounded corners
    // but straigtens out the edges. One way to do this is to clamp our regional coordinates. If we use
    // the max() function we eliminate the circle and instead create a rounded corner for each region. 
    d = length(max(abs(uv) - center, 0.0));
    
    // uncomment this line to see the result
//    color = half3(step(radius, d)) * palette(d + radius);
    
    // If this is hard to visualize, remove the abs to turn this back to "one region".
    // See if this helps you understand how max() helping us.
    d = length(max(uv - center, 0.0));
    
    // uncomment this line to see the result
    //color = half3(step(radius, d));
    
    // Now we have everything we need, lets bring back fract + our color palette to get an 
    // awesome looking rainbow round rect pattern
    d = length(max(abs(uv) - center, 0.0));
    
    // uncomment this line to see the result
    //color = half3(fract(d * 20) * palette(d));
    
    // We can also play with other patterns by using sin
    
    // uncomment this line to see the result
    //color = half3(abs(sin(d * 100)) * palette(d));
    
    return half4(color, 1.0);
}
""".trimIndent()

@Preview
@Composable
fun RoundedRectPlayground() {
  val shader = rememberShader(shader = roundedRectShader)
  SimpleSketchWithCache(
    modifier = Modifier
      .fillMaxWidth()
      .aspectRatio(1f)
  ) {
    with(shader) {
      setFloatUniform(
        "resolution",
        size.width,
        size.height
      )
      setFloatUniform(
        "time",
        it.value
      )
    }
    onDrawBehind {
      drawRect(brush = ShaderBrush(shader))
    }
  }
}