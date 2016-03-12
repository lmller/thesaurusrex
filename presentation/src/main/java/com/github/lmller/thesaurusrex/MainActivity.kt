package com.github.lmller.thesaurusrex

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), SearchView
{
	private var resultsView: TextView? = null
	private var inputText: EditText? = null

	//TODO inject
	private val presenter = SearchPresenter(this)

	private var subscription: Subscription? = null

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		resultsView = findViewById(R.id.results) as TextView
		inputText = findViewById(R.id.input) as EditText

		subscription = Observable.create<String>
		{
			inputText?.addTextChangedListener(object : TextWatcher
			{
				override fun afterTextChanged(p0: Editable?) = Unit
				override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
				override fun onTextChanged(enteredWord: CharSequence, p1: Int, p2: Int, p3: Int)
				{
					if (enteredWord.length > 2)
					{
						Log.d("T-Rex", "callOnNext")
						it.onNext(enteredWord.toString())
					}
				}
			})
		}
				.debounce (1000, TimeUnit.MILLISECONDS)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(presenter.searchWordChangeSubscriber)

	}

	override fun updateSearchResults(result: String)
	{
		resultsView?.append(result + "\n")

	}

	override fun clearSearchResults()
	{
		resultsView?.text = ""
	}

	override fun onPause()
	{
		presenter.onPause()
	}

	override fun onDestroy()
	{
		super.onDestroy()
		subscription?.unsubscribe()
		presenter.onDestroy()
	}
}

