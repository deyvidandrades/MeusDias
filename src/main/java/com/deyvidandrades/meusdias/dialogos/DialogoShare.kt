package com.deyvidandrades.meusdias.dialogos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.deyvidandrades.meusdias.R
import com.deyvidandrades.meusdias.adaptadores.AdaptadorShare
import com.deyvidandrades.meusdias.assistentes.AssistenteViewToBitmap
import com.deyvidandrades.meusdias.interfaces.OnItemClickListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DialogoShare : BottomSheetDialogFragment(), OnItemClickListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val arrayCards: ArrayList<HashMap<String, String>> = ArrayList()
        val dialogoView = inflater.inflate(R.layout.dialogo_share, container, false)

        arrayCards.add(hashMapOf("bg" to "#211C2E", "accent" to "#9C99F7", "text" to "#E2E1E8"))
        arrayCards.add(hashMapOf("bg" to "#E2E1E8", "accent" to "#9C99F7", "text" to "#211C2E"))
        arrayCards.add(hashMapOf("bg" to "#043426", "accent" to "#79F6D0", "text" to "#EEEEEE"))
        arrayCards.add(hashMapOf("bg" to "#021336", "accent" to "#75A1FA", "text" to "#EEEEEE"))
        arrayCards.add(hashMapOf("bg" to "#480519", "accent" to "#F6799F", "text" to "#FBCBD9"))
        arrayCards.add(hashMapOf("bg" to "#4A3203", "accent" to "#FACE75", "text" to "#FEF1D8"))

        val recyclerCards: RecyclerView = dialogoView.findViewById(R.id.recyclerShare)
        val layoutManagerDisciplinas =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val adaptadorDisciplinas = AdaptadorShare(requireContext(), arrayCards, this)

        recyclerCards.setHasFixedSize(false)
        recyclerCards.adapter = adaptadorDisciplinas
        recyclerCards.layoutManager = layoutManagerDisciplinas

        val snapCategorias: SnapHelper = PagerSnapHelper()
        snapCategorias.attachToRecyclerView(recyclerCards)

        return dialogoView
    }

    override fun onItemClick(view: View) {
        val bitmapURI =
            AssistenteViewToBitmap.getViewToBitmapURI(requireContext(), view)

        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, bitmapURI.toString().toUri())
            type = "image/jpeg"
        }
        startActivity(Intent.createChooser(shareIntent, null))

        dismiss()
    }
}