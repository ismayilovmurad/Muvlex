package com.martiandeveloper.muvlex.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.model.ExplorePost
import kotlinx.coroutines.tasks.await

class ExplorePostSource(
    private val firebaseFirestore: FirebaseFirestore
) : PagingSource<QuerySnapshot, ExplorePost>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, ExplorePost> {

        return try {
            val currentPage =
                params.key ?: firebaseFirestore.collection("posts")
                    .orderBy("time", Query.Direction.DESCENDING)
                    .limit(10).get()
                    .await()

            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]

            LoadResult.Page(
                data = currentPage.toObjects(ExplorePost::class.java),
                prevKey = null,
                nextKey = firebaseFirestore.collection("posts")
                    .orderBy("time", Query.Direction.DESCENDING)
                    .limit(10)
                    .startAfter(lastDocumentSnapshot).get().await()
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, ExplorePost>): QuerySnapshot? {
        TODO("Not yet implemented")
    }

}
