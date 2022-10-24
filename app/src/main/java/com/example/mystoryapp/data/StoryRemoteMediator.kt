package com.example.mystoryapp.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.mystoryapp.data.local.database.StoryDatabase
import com.example.mystoryapp.data.local.entity.RemoteKeys
import com.example.mystoryapp.data.local.entity.StoryEntity
import com.example.mystoryapp.data.remote.retrofit.ApiService
import com.example.mystoryapp.utlis.wrapEspressoIdlingResource

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator (
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val token:String ): RemoteMediator<Int, StoryEntity>(){

    companion object{
        const val INITIAL_PAGE_SIZE = 1
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH

    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {
        val page = when(loadType){
            LoadType.REFRESH->{
                val remoteKeys = getRemoteKeysCloseToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_SIZE
            }
            LoadType.PREPEND->{
                val remoteKeys = getRemoteKeysFirstTime(state)
                val prefKeys = remoteKeys?.prevKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prefKeys
            }
            LoadType.APPEND->{
                val remoteKeys = getRemoteKeysLastTime(state)
                val nextKeys = remoteKeys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKeys
            }
        }

        wrapEspressoIdlingResource {
            try {
                val responseData = apiService.getStories(token, page, state.config.pageSize, 0)
                val endOfPaginationReached = responseData.listStory.isEmpty()


                database.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        database.remoteKeysDao().deleteRemoteKeys()
                        database.storyDao().deleteAll()
                    }
                    val prefKeys = if (page == 1) null else page - 1
                    val nextKeys = if (endOfPaginationReached) null else page + 1
                    val keys = responseData.listStory.map {
                        RemoteKeys(id = it.id, prevKey = prefKeys, nextKey = nextKeys)
                    }
                    // Save Remote keys to the local database
                    database.remoteKeysDao().insertAll(keys)

                    // Convert GetStoriesResponse class to StoryEntity class
                    // We need to convert because the response from API is different from local database Entity
                    val lisStory = mutableListOf<StoryEntity>()
                    responseData.listStory.forEach {
                        lisStory.add(
                            StoryEntity(
                                it.id,
                                it.photoUrl,
                                it.createdAt,
                                it.name,
                                it.description,
                                it.lat,
                                it.lon
                            )
                        )
                    }
                    // Save Story to the local database
                    database.storyDao().insertStory(lisStory)
                }
                return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            } catch (exception: Exception) {
                return MediatorResult.Error(exception)
            }
        }
    }

    private suspend fun getRemoteKeysLastTime(state: PagingState<Int, StoryEntity>): RemoteKeys?{
        return state.pages.lastOrNull{it.data.isNotEmpty()}?.data?.lastOrNull()?.let { data->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeysFirstTime(state: PagingState<Int, StoryEntity>): RemoteKeys?{
        return state.pages.firstOrNull{it.data.isNotEmpty()}?.data?.firstOrNull()?.let { data->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeysCloseToCurrentPosition(state: PagingState<Int, StoryEntity>): RemoteKeys?{
        return state.anchorPosition?.let {position->
            state.closestItemToPosition(position)?.id?.let { id->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }

    }

}