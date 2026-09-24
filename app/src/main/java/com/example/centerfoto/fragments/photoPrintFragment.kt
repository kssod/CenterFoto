package com.example.centerfoto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.centerfoto.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PhotoPrintFragment() : BottomSheetDialogFragment() {
    companion object {
        fun newInstance(): PhotoPrintFragment {
            val fragment = PhotoPrintFragment()
            return fragment
        }
    }

    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?, savedInstantState: Bundle?) : View? {
        return inflater.inflate(R.layout.fragment_photo_print, container, false)
    }


}