package com.github.lmller.thesaurusrex

import android.app.Activity

interface Presenter
{
	fun onPause() = Unit
	fun onStart() = Unit
	fun onDestroy() = Unit
	fun onResume() = Unit
}