package com.example.savvy_android.touch

import android.graphics.Canvas
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.savvy_android.R
import com.example.savvy_android.adapter.TravelPlanListAdapter
import java.lang.Float.max
import java.lang.Float.min

class PlanItemTouchCallback : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
    ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
) {
    private var currentPosition: Int? = null
    private var clamp = 0f
    private var currentDx = 0f

    // 삭제버튼 width 구하는 함수
    private fun getViewWidth(viewHolder: RecyclerView.ViewHolder): Float {
        val viewWidth =
            (viewHolder as TravelPlanListAdapter.PlanViewHolder).itemView.findViewById<ConstraintLayout>(
                R.id.item_plan_hide_o
            ).width
        return viewWidth.toFloat()
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            viewHolder?.let {
                // 삭제 버튼 width 획득
                clamp = getViewWidth(viewHolder)
                currentPosition = viewHolder.adapterPosition
            }
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    //스와이프 될 레이아웃만 스와이프 되도록 구현
    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean,
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val view = (viewHolder as TravelPlanListAdapter.PlanViewHolder).hideX

            val isClamped = getTag(viewHolder)

            val x = clampViewPositionHorizontal(view, dX, isClamped, isCurrentlyActive)

            currentDx = x

            getDefaultUIUtil().onDraw(
                c, recyclerView, view, x, dY, actionState, isCurrentlyActive
            )
        }
    }

    //상호작용 종료 및 애니메이션 종료 후 호출
    override fun clearView(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ) {
        super.clearView(recyclerView, viewHolder)

        val view = (viewHolder as TravelPlanListAdapter.PlanViewHolder).hideO

        view.isClickable = currentDx == -clamp
        Log.e("TEST","${view.isClickable}")
    }


    //드래그 및 스와이프 방향을 제어. 드래그는 사용하지 않고, 양방향 스와이프를 사용
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean {
        return false
    }

    //Swipe시 이벤트
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    // swipe될 뷰 (우리가 스와이프할 시 움직일 화면)
    private fun getView(viewHolder: RecyclerView.ViewHolder): View {
        return (viewHolder as TravelPlanListAdapter.PlanViewHolder).itemView.findViewById(R.id.item_plan_hide_x)
    }

    // view의 tag로 스와이프 고정됐는지 안됐는지 확인 (고정 == true)
    private fun getTag(viewHolder: RecyclerView.ViewHolder): Boolean {
        return viewHolder.itemView.tag as? Boolean ?: false
    }

    // view의 tag에 스와이프 고정됐으면 true, 안됐으면 false 값 넣기
    private fun setTag(viewHolder: RecyclerView.ViewHolder, isClamped: Boolean) {
        viewHolder.itemView.tag = isClamped
    }

    // 스와이프 될 가로(수평) 길이
    private fun clampViewPositionHorizontal(
        view: View,
        dX: Float,  //
        isClamped: Boolean,
        isCurrentlyActive: Boolean,
    ): Float {
        val maxSwipe: Float = -clamp * 1.5f

        val right = 0f

        val x = if (isClamped) {
            if (isCurrentlyActive) dX - clamp else -clamp
        } else dX

        return min(
            max(maxSwipe, x),
            right
        )
    }

    // 사용자가 Swipe 동작으로 간주할 최소 속도
    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return defaultValue * 10
    }

    // 사용자가 스와이프한 것으로 간주할 view 이동 비율
    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        setTag(viewHolder, currentDx <= -clamp)
        return 2f
    }
}