package com.martiandeveloper.muvlex.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.martiandeveloper.muvlex.model.Post
import com.martiandeveloper.muvlex.utils.PAGE_SIZE
import kotlinx.coroutines.tasks.await

class HomePostDataSource(
    private val friendList: ArrayList<Any>,
    private val firebaseFirestore: FirebaseFirestore
) : PagingSource<QuerySnapshot, Post>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Post> {

        return try {
            val currentPage =
                params.key ?: firebaseFirestore.collection("posts")
                    .whereIn("user", friendList)
                    .orderBy("time", Query.Direction.DESCENDING)
                    .limit(PAGE_SIZE.toLong()).get()
                    .await()

            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]

            LoadResult.Page(
                data = currentPage.toObjects(Post::class.java),
                prevKey = null,
                nextKey = firebaseFirestore.collection("posts")
                    .whereIn("user", friendList)
                    .orderBy("time", Query.Direction.DESCENDING)
                    .limit(PAGE_SIZE.toLong())
                    .startAfter(lastDocumentSnapshot).get().await()
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, Post>): QuerySnapshot? {
        return state.pages.last().nextKey
    }

}
