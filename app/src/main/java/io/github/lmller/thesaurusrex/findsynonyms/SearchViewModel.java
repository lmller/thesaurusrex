package io.github.lmller.thesaurusrex.findsynonyms;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.github.lmller.thesaurusrex.BR;
import io.github.lmller.thesaurusrex.Synset;
import io.github.lmller.thesaurusrex.Term;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.OnErrorNotImplementedException;
import io.reactivex.schedulers.Schedulers;

import static io.github.lmller.thesaurusrex.ListUtil.join;
import static io.github.lmller.thesaurusrex.ListUtil.map;

public class SearchViewModel extends BaseObservable {
	private final Thesaurus synonyms;
	private final Disposable disposable;

	private boolean isLoading;
	private List<Term> searchResults = Collections.emptyList();
	private String errorMessage;

	public SearchViewModel(Thesaurus synonymSource, Observable<String> queryTextChange) {
		this(synonymSource, queryTextChange, Schedulers.single());
	}

	public SearchViewModel(Thesaurus synonymSource, Observable<String> queryTextChange,
			Scheduler scheduler) {
		this.synonyms = synonymSource;

		disposable = queryTextChange
				.filter(text -> !text.isEmpty())
				.debounce(250, TimeUnit.MILLISECONDS, scheduler)
				.flatMap(text -> synonyms.findSynonym(text)
						.doOnSubscribe(s -> {
							isLoading = true;
							resetState();
						})
						.map(synonyms -> new SearchResult(synonyms))
						.onErrorReturn(error -> new SearchResult(error))
						.doOnTerminate(() -> isLoading = false)
				).subscribe(result -> {
					if (result.isError()) {
						errorMessage = result.error.getMessage();

						notifyPropertyChanged(BR.errorVisible);
						notifyPropertyChanged(BR.errorMessage);
					} else {
						searchResults.addAll(result.synonyms.getTerms());
						notifyPropertyChanged(BR.searchResults);
					}
				}, e -> {
					throw new OnErrorNotImplementedException(e);
				});
	}

	private void resetState() {
		searchResults = new ArrayList<>();
		errorMessage = null;
		notifyPropertyChanged(BR.errorVisible);
	}

	@Bindable
	public String getSearchResults() {
		final List<String> terms = map(searchResults, t -> t.getTerm());
		return join(terms, '\n');
	}

	@Bindable
	public boolean isProgressBarVisible() {
		return isLoading;
	}

	@Bindable
	public boolean isErrorVisible() {
		return errorMessage != null;
	}

	@Bindable
	public String getErrorMessage() {
		return errorMessage;
	}

	public void onDestroy() {
		disposable.dispose();
	}

	private static class SearchResult {
		Throwable error;
		Synset synonyms;

		SearchResult(Throwable error) {
			this.error = error;
		}

		SearchResult(Synset synonyms) {
			this.synonyms = synonyms;
		}

		boolean isError() {
			return error != null;
		}
	}


}
