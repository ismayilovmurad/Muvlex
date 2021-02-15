package com.martiandeveloper.muvlex.view.feed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.utils.navigate

class UserFragment : Fragment() {

    private val args: UserFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)

        view.findViewById<MaterialButton>(R.id.fragment_user_followBTN).setOnClickListener {

            val userMap = hashMapOf(
                "following" to arrayListOf(args.userId),
            )

            Firebase
                .firestore
                .collection("users")
                .document(Firebase.auth.currentUser!!.uid)
                .update("following", FieldValue.arrayUnion(args.userId))


            Toast.makeText(context,args.userId,Toast.LENGTH_SHORT).show()

        }

        return view
    }

}