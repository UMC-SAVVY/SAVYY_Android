package com.example.savvy_android.diary.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ItemDiaryMakeBinding
import com.example.savvy_android.diary.data.DiaryDetailItemData
import com.example.savvy_android.diary.activity.DiaryMake3Activity
import com.example.savvy_android.utils.place.PlaceAddActivity


class Make3Adapter(
    private val context: Context,
    private var diaryViewData: ArrayList<DiaryDetailItemData>,
) :
    RecyclerView.Adapter<Make3Adapter.DiaryViewHolder>() {
    // 각 뷰들을 binding 사용하여 View 연결
    inner class DiaryViewHolder(binding: ItemDiaryMakeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var text = binding.itemDiaryViewTv
        var imageLayout = binding.itemDiaryViewImgLayout
        var image = binding.itemDiaryViewImg
        var imageDelete = binding.itemDiaryDeleteCv
        var placeCard = binding.itemDiaryPlaceLayout
        var placeIcon = binding.itemDiaryPlaceIcon
        var placeName = binding.itemDiaryPlaceName
    }

    // View 생성될 때 호출되는 method
    // View 생성, RecyclerView가 생성될 때 호출
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): DiaryViewHolder {
        val binding =
            ItemDiaryMakeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiaryViewHolder(binding)
    }

    // View 바인드 될 때 호출되는 method
    // View에 내용이 작성되는 method, 스크롤을 올리거나 내릴 때마다 호출
    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        val data = diaryViewData[position]
        val hasText = data.isText
//        val hasPlace = data.hasPlace

        // 텍스트 or 이미지 인지
        if (hasText) {
            // 텍스트 경우
            holder.text.visibility = View.VISIBLE   // 텍스트 보이게
            holder.imageLayout.visibility = View.GONE // 이미지 안 보이게
        } else {
            // 이미지 경우
            holder.text.visibility = View.GONE   // 텍스트 안 보이게
            holder.imageLayout.visibility = View.VISIBLE    // 이미지 보이게
            Glide.with(holder.itemView)
                .load(data.image)
                .into(holder.image)

            // 장소가 장소 저장함에 있을 때
//            if (hasPlace!!) {
//                holder.placeIcon.setImageResource(R.drawable.ic_map)
//                holder.placeName.text = data.placeName  // 장소 이름
//            } else {
//                holder.placeIcon.setImageResource(R.drawable.ic_plus_round)
//                holder.placeName.text = "장소 정보 입력하기"
//            }

            // 이미지 삭제 클릭 이벤트
            holder.imageDelete.setOnClickListener {
                removeDiary(position)
            }

            // 장소 cardView 클릭 이벤트
            holder.placeCard.setOnClickListener {
//                if (hasPlace) {
//                    holder.placeIcon.setImageResource(R.drawable.ic_plus_round)
//                    holder.placeName.text = "장소 정보 입력하기"
//                } else {
//                    val intent = Intent(holder.itemView.context, PlaceAddActivity::class.java)
//                    (holder.itemView.context as DiaryMake3Activity).startActivityForResult(intent, 100)
//                }

                // EditText에 포커스가 없는 경우에만 동작하도록
                if (!holder.placeName.hasFocus()) {
                    holder.placeName.isEnabled = true
                    holder.placeName.setText("")
                    holder.placeName.hint = R.string.plz_enter_place_name.toString()
                    holder.placeName.requestFocus() // EditText에 포커스를 주기 위한 메소드

                    // 필요에 따라 키보드를 자동으로 올릴 수도 있습니다.
                    val imm =
                        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(holder.placeName, InputMethodManager.SHOW_IMPLICIT)
                }
            }

            // 장소 이름 작성 이벤트
            holder.placeName.setOnKeyListener { v, keyCode, event ->
                // 키 이벤트가 ACTION_DOWN이고, 눌린 키가 엔터 키(KEYCODE_ENTER)일 경우
                if (event.action == KeyEvent.ACTION_DOWN && (keyCode == KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_BACK)) {
                    // 장소 이름 입력란에서 입력된 텍스트 가져오기
                    val tempName = holder.placeName.text
                    if (tempName.isNotEmpty()) {
                        // 입력된 장소 이름을 데이터에 저장하고, 힌트(placeholder) 비우기
                        data.placeName = tempName.toString()
                        holder.placeName.isEnabled = false
                        holder.placeName.hint = ""
                    } else {
                        // 장소 이름이 비어있을 경우, 특정 처리를 수행
                        holder.placeName.isEnabled = false
                        holder.placeName.setText(R.string.plz_enter_place_name.toString())
                        holder.placeName.hint = R.string.plz_enter_place_name.toString()
                    }
                    // 이벤트 처리 완료를 알리기 위해 true 반환
                    true
                } else {
                    false
                }
            }
        }
    }

    // 리스트의 수 count
    override fun getItemCount(): Int = diaryViewData.size

    // 데이터 추가
    fun addDiary(insertData: DiaryDetailItemData) {
        diaryViewData.add(insertData)
        notifyItemInserted(diaryViewData.size)
        Log.e("TEST", "$diaryViewData")
    }

    // 데이터 삭제
    private fun removeDiary(position: Int) {
        if (position >= 0 && position < diaryViewData.size) {
            diaryViewData.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
