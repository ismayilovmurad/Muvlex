package com.martiandeveloper.muvlex.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.model.UserPost
import kotlinx.coroutines.tasks.await

class UserPostSource(
    private val firebaseFirestore: FirebaseFirestore,
    private val uid: String
) : PagingSource<QuerySnapshot, UserPost>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, UserPost> {

        return try {
            val currentPage =
                params.key ?: firebaseFirestore.collection("posts")
                    .orderBy("time", Query.Direction.DESCENDING)
                    .whereEqualTo("uid", uid)
                    .limit(20).get()
                    .await()

            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]

            LoadResult.Page(
                data = currentPage.toObjects(UserPost::class.java),
                prevKey = null,
                nextKey = firebaseFirestore.collection("posts")
                    .orderBy("time", Query.Direction.DESCENDING)
                    .whereEqualTo("uid", uid)
                    .limit(20)
                    .startAfter(lastDocumentSnapshot).get().await()
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, UserPost>): QuerySnapshot? {
        TODO("Not yet implemented")
    }

}
