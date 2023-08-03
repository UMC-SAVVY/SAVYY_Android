package com.example.savvy_android.diary.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.savvy_android.R
import com.example.savvy_android.databinding.ItemDiaryMakeBinding
import com.example.savvy_android.diary.activity.DiaryMake3Activity
import com.example.savvy_android.diary.data.detail.DiaryContent


class Make3Adapter(
    context: Context,
    private var diaryViewData: ArrayList<DiaryContent>,
) :
    RecyclerView.Adapter<Make3Adapter.DiaryViewHolder>() {
    private val imm =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

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
        val data = diaryViewData[holder.adapterPosition]
        val type = data.type
        var content = data.content
        var placeName = data.location
        // val hasPlace = data.hasPlace

        // 텍스트 or 이미지 인지
        if (type == "text") {
            // 텍스트 경우
            holder.text.visibility = View.VISIBLE   // 텍스트 보이게
            holder.imageLayout.visibility = View.GONE // 이미지 안 보이게
            holder.text.setText(content)
            holder.text.requestFocus() // EditText에 포커스를 주기 위한 메소드

            holder.text.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val tempText = holder.text.text
                    if (tempText.isNotEmpty()) {
                        // 입력된 내용을 데이터에 저장하고, 힌트(placeholder) 비우기
                        data.content = tempText.toString()
                        holder.placeName.hint = ""
                    } else {
                        // 내용이 비어있을 경우, 특정 처리를 수행
                        holder.placeName.hint = "내용을 작성해주세요"
                    }
                }
            }
        } else {
            // 이미지 경우
            holder.text.visibility = View.GONE   // 텍스트 안 보이게
            holder.imageLayout.visibility = View.VISIBLE    // 이미지 보이게
            Glide.with(holder.itemView)
                .load(content)
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
                holder.imageDelete.isClickable = false
                removeDiary(holder.adapterPosition)
                DiaryMake3Activity.imageCount--
                holder.imageDelete.isClickable = true
            }

            // 장소 cardView 클릭 이벤트
            holder.placeCard.setOnClickListener {
                // EditText에 포커스가 없는 경우에만 동작하도록
                if (!holder.placeName.hasFocus()) {
                    holder.placeName.isEnabled = true
                    holder.placeName.setText("")
                    holder.placeName.hint = "장소 정보 입력하기"
                    holder.placeName.requestFocus() // EditText에 포커스를 주기 위한 메소드

                    // 필요에 따라 키보드를 자동으로 올릴 수도 있음
                    imm.showSoftInput(holder.placeName, InputMethodManager.SHOW_IMPLICIT)
                }

                // 여기는 장소 관련 내용
//                if (hasPlace) {
//                    holder.placeIcon.setImageResource(R.drawable.ic_plus_round)
//                    holder.placeName.text = "장소 정보 입력하기"
//                } else {
//                    val intent = Intent(holder.itemView.context, PlaceAddActivity::class.java)
//                    (holder.itemView.context as DiaryMake3Activity).startActivityForResult(intent, 100)
//                }
            }

            // 장소 이름 입력에서 포커스 변화가 생겼을 때
            holder.placeName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    // 포커스를 잃었을 때 실행할 코드 작성
                    val tempName = holder.placeName.text
                    if (tempName.isNotEmpty()) {
                        // 입력된 장소 이름을 데이터에 저장하고, 힌트(placeholder) 비우기
                        data.location = tempName.toString()
                        holder.placeName.isEnabled = false
                        holder.placeName.hint = ""
                    } else {
                        // 장소 이름이 비어있을 경우, 특정 처리를 수행
                        holder.placeName.isEnabled = false
                        holder.placeName.setText(R.string.plz_enter_place_name)
                        holder.placeName.hint = "장소 정보 입력하기"
                    }
                }
            }
        }

    }

    // 리스트의 수 count
    override fun getItemCount(): Int = diaryViewData.size

    // 데이터 추가
    fun addDiary(insertData: DiaryContent) {
        diaryViewData.add(insertData)
        notifyItemInserted(diaryViewData.size)
    }

    // 데이터 삭제
    private fun removeDiary(position: Int) {
        if (position >= 0 && position < diaryViewData.size) {
            diaryViewData.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
