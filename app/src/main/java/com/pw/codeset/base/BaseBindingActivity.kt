package com.pw.codeset.base

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.pw.other.annotation.inject.InjectUtils

abstract class BaseBindingActivity<T : ViewBinding> : BaseActivity() {
    protected lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = initViewBinding()
        setContentView(binding.root)
        InjectUtils.injectView(this)
        initHeader()
        initView()
        isCreating = true
    }

    override fun create() {

    }
    abstract fun initViewBinding(): T

    override fun getContentId(): Int {
        return 0
    }
}