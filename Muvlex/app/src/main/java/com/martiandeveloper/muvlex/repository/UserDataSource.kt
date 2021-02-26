package com.martiandeveloper.muvlex.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.martiandeveloper.muvlex.model.User
import com.martiandeveloper.muvlex.utils.PAGE_SIZE
import kotlinx.coroutines.tasks.await

class UserDataSource(
    private val query: String, private val firebaseFirestore: FirebaseFirestore
) : PagingSource<QuerySnapshot, User>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, User> {

        return try {
            val currentPage =
                params.key ?: firebaseFirestore.collection("users")
                    .whereGreaterThanOrEqualTo("username", query)
                    .limit(PAGE_SIZE.toLong()).get()
                    .await()

            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]

            LoadResult.Page(
                data = currentPage.toObjects(User::class.java),
                prevKey = null,
                nextKey = firebaseFirestore.collection("users")
                    .whereGreaterThanOrEqualTo("username", query)
                    .limit(PAGE_SIZE.toLong())
                    .startAfter(lastDocumentSnapshot).get().await()
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, User>): QuerySnapshot? {
        return state.pages.last().nextKey
    }

}
