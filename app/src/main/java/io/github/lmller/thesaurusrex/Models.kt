package io.github.lmller.thesaurusrex

import java.util.Collections.emptyList

data class MetaData(
        val apiVersion: String? = null,
        val warning: String? = null,
        val copyright: String? = null,
        val license: String? = null,
        val source: String? = null,
        val date: String? = null
)

data class SynonymList(
        val metaData: MetaData? = MetaData(),
        val synsets: List<Synset> = emptyList()
)

data class Synset(
        val id: Long = 0L,
        val categories: List<Category> = emptyList(),
        val terms: List<Term> = emptyList()
)

data class Term(val term: String)

data class Category(val name: String)