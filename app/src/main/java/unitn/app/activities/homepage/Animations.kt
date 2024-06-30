package unitn.app.activities.homepage

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.example.test.R
import kotlin.math.absoluteValue
import kotlin.random.Random

class Animations(private val context: Context) {

    private val numberOfAnimations = 4;

    fun moveOut(): Animation {
        val animation = AnimationUtils.loadAnimation(context, R.anim.move_fade_out);
        return animation;
    }

    fun fadeOut(): Animation {
        val animation = AnimationUtils.loadAnimation(context, R.anim.fade_out);
        return animation;
    }

    fun zoomOut(): Animation {
        val animation = AnimationUtils.loadAnimation(context, R.anim.spiral_inwards);
        return animation;
    }

    fun spiralOutOfControl(): Animation {
        val animation = AnimationUtils.loadAnimation(context, R.anim.spiral_outwards);
        return animation;
    }

    fun getRandomAnimation(): Animation {
        val randomValue = Random.nextInt().absoluteValue % numberOfAnimations;
        return when (randomValue) {
            0 -> {
                moveOut();
            }

            1 -> {
                fadeOut();
            }

            2 -> {
                spiralOutOfControl();
            }

            3 -> {
                zoomOut();
            }

            else -> {
                fadeOut();
            }

        }
    }
}