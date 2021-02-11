package com.martiandeveloper.muvlex.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.model.ProfilePost
import kotlinx.coroutines.tasks.await

class ProfilePostSource(
    private val firebaseFirestore: FirebaseFirestore
) : PagingSource<QuerySnapshot, ProfilePost>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, ProfilePost> {

        return try {
            val currentPage =
                params.key ?: firebaseFirestore.collection("posts")
                    .orderBy("time", Query.Direction.DESCENDING)
                    .whereEqualTo("user_id", Firebase.auth.currentUser!!.uid)
                    .limit(10).get()
                    .await()

            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]

            LoadResult.Page(
                data = currentPage.toObjects(ProfilePost::class.java),
                prevKey = null,
                nextKey = firebaseFirestore.collection("posts")
                    .orderBy("time", Query.Direction.DESCENDING)
                    .whereEqualTo("user_id", Firebase.auth.currentUser!!.uid)
                    .limit(10)
                    .startAfter(lastDocumentSnapshot).get().await()
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, ProfilePost>): QuerySnapshot? {
        TODO("Not yet implemented")
    }

}
