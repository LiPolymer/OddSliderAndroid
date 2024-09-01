package ink.lipoly.oddslider


import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import ink.lipoly.oddslider.ui.theme.OddSliderAndroidTheme
import kotlin.math.roundToInt
import kotlin.system.exitProcess


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OddSliderAndroidTheme {
                CheckPerm()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    OddSlider(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CheckPerm(){
    if (!Settings.System.canWrite(LocalContext.current)) {
        val intent = Intent(
            Settings.ACTION_MANAGE_WRITE_SETTINGS,
            Uri.parse("package:ink.lipoly.oddslider")
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(LocalContext.current,intent,null)
        exitProcess(0)
    }
}

var IsBright = false
var IsInit = false
var IsShowed = false

@Composable
fun OddSlider(modifier: Modifier = Modifier) {
    val crs = currentRecomposeScope
    val context = LocalContext.current
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    var offsetX by remember {
        mutableFloatStateOf(0f)
    }
    val boxSideLengthDp = 30.dp
    val sliderLengthMulti = 10
    val sliderLength = boxSideLengthDp * sliderLengthMulti
    val boxSlideLengthPx = with(LocalDensity.current) {
        boxSideLengthDp.toPx()
    }
    val draggableState = rememberDraggableState {
        offsetX = (offsetX + it).coerceIn(0f, (sliderLengthMulti - 1)* boxSlideLengthPx)
    }
    val imgFlag = painterResource(R.drawable.flag)
    val imgBar = painterResource(R.drawable.bar)

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxHeight()
        ){
//            Text(
//                text = "sliderLength:${sliderLength - boxSideLengthDp} + $boxSideLengthDp = $sliderLength"
//            )//debug
            Text(
                text = "offsetX:$offsetX",
                color = Color.Transparent
            )//debug
            Box(
                Modifier
                    .width(sliderLength)
                    .height(boxSideLengthDp)
            ) {
                Box(
                    Modifier
                        .size(boxSideLengthDp)
                        .offset {
                            IntOffset(offsetX.roundToInt(), 0)
                        }
                        .draggable(
                            orientation = Orientation.Horizontal,
                            state = draggableState
                        )
                ){
                    Image(
                        painter = imgFlag,
                        alignment = Alignment.Center,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    )
                }
            }
            Box(
                modifier = Modifier
                    .width(sliderLength)
                    .height(30.dp)
            ){
                Image(
                    painter = imgBar,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .width(sliderLength)
                        .fillMaxWidth()
                        .fillMaxHeight()
                )
            }
            if (IsInit){
                if (IsBright){
                    val target = offsetX/ with(LocalDensity.current){(sliderLength - boxSideLengthDp).toPx()}
                    Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, (target * 255f).toInt())
                }else{
                    val target = offsetX/ with(LocalDensity.current){(sliderLength - boxSideLengthDp).toPx()}
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,(target * audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()).toInt(),AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
                }
            }else{
                if (IsBright){
                    val brightness = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, -1).toFloat() / 255f
                    offsetX = with(LocalDensity.current){((sliderLength - boxSideLengthDp) * brightness).toPx()}.toFloat()
                    //Text(text = brightness.toString())//debug
                }else{
                    val volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() / audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
                    offsetX = with(LocalDensity.current){((sliderLength - boxSideLengthDp) * volume).toPx()}.toFloat()
                    //Text(text = "${audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)}|${audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)}|$volume")//debug
                }
            }
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.width(sliderLength)
            ){
                //Button
                if (IsBright){
                    Button(onClick = {
                        IsBright = false
                        IsInit = false
                        IsShowed = false
                        crs.invalidate()
                    },
                        modifier = Modifier.padding(14.dp,0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Build,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }else{
                    Button(onClick = {
                        IsBright = true
                        IsInit = false
                        IsShowed = false
                        crs.invalidate()
                    },
                        modifier = Modifier.padding(14.dp,0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Build,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }

                if (!IsShowed){
                    val ct = if(IsBright){
                        "亮度"
                    }else{
                        "音量"
                    }
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = ct,
                            modifier = Modifier.height(boxSideLengthDp)
                        )
                    }
                }
            }
        }
    }
    if (IsInit){
        IsShowed = true
    }
    IsInit = true
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun GreetingPreview() {
    OddSliderAndroidTheme {
        OddSlider()
    }
}