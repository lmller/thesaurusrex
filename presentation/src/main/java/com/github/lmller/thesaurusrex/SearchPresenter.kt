package com.github.lmller.thesaurusrex

import android.app.Activity
import android.util.Log
import data.OpenThesaurus
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import search.FindSynonyms

class SearchPresenter(private val view: SearchView) : Presenter
{
	//TODO inject
	private val synonyms: FindSynonyms = FindSynonyms(OpenThesaurus());
	private var subscription : Subscription? = null

	override fun onPause()
	{
		subscription?.unsubscribe()
	}

	val searchWordChangeSubscriber = object: Subscriber<String>()
	{
		override fun onCompleted() = Unit

		override fun onError(e: Throwable?)
		{
			Log.e("T-Rex", "onError#1", e)
		}

		override fun onNext(enteredWord: String)
		{
			view.clearSearchResults()
			subscription?.unsubscribe()
			subscription = synonyms.findSynonyms(enteredWord)
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(object : Subscriber<String>()
					{
						override fun onError(e: Throwable?)
						{
							Log.e("T-Rex", "onError#2", e)
						}

						override fun onCompleted() = Unit

						override fun onNext(string: String)
						{
							view.updateSearchResults(string)
						}

					})
		}
	}



}