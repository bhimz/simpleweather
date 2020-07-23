package com.bhimz.simpleweather.matcher

import android.content.res.Resources
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

//inspired by dannyroa's RecyclerViewMatcher, went and make a Koltin one
class RecyclerViewMatcher(private val id: Int) {

    fun atPosition(position: Int) = atPositionOnView(position, -1)

    fun atPositionOnView(position: Int, targetViewId: Int): Matcher<View> =
        object : TypeSafeMatcher<View>() {
            var resources: Resources? = null
            override fun describeTo(description: Description?) {
                val resourceIdName = resources?.let {
                    try {
                        it.getResourceName(id)
                    } catch (e: Exception) {
                        id.toString()
                    }
                } ?: id.toString()
                description?.appendText("with id $resourceIdName")
            }

            override fun matchesSafely(item: View?): Boolean {
                val view = item ?: return false
                resources = view.resources
                val recyclerView = view.rootView.findViewById<RecyclerView?>(id)
                val childView = recyclerView?.findViewHolderForAdapterPosition(position)?.itemView
                return when {
                    recyclerView == null -> false
                    recyclerView.id != id -> false
                    targetViewId == -1 -> view == childView
                    else -> view == childView?.findViewById(targetViewId)

                }
            }
        }
}

fun withRecyclerView(id: Int) =
    RecyclerViewMatcher(id)