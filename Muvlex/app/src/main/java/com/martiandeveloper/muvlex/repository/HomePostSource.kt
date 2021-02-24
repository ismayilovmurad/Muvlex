package com.martiandeveloper.muvlex.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.martiandeveloper.muvlex.model.ExplorePost
import com.martiandeveloper.muvlex.model.HomePost
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class HomePostSource(
    private val firebaseFirestore: FirebaseFirestore,
    private val friendList: ArrayList<Any>
) : PagingSource<QuerySnapshot, HomePost>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, HomePost> {

        return try {

            val currentPage =
                params.key ?: firebaseFirestore.collection("posts").whereIn("user_id", friendList)
                    .orderBy("time", Query.Direction.DESCENDING)
                    .limit(10).get()
                    .await()

            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]

            LoadResult.Page(
                data = currentPage.toObjects(HomePost::class.java),
                prevKey = null,
                nextKey = firebaseFirestore.collection("posts").whereIn("user_id", friendList)
                    .orderBy("time", Query.Direction.DESCENDING)
                    .limit(10)
                    .startAfter(lastDocumentSnapshot).get().await()
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, HomePost>): QuerySnapshot? {
        TODO("Not yet implemented")
    }

}
