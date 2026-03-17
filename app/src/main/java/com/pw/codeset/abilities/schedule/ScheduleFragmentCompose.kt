package com.pw.codeset.abilities.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.MutableLiveData
import com.pw.baseutils.utils.NStringUtils
import com.pw.codeset.R
import com.pw.codeset.application.MyApp
import com.pw.codeset.databean.ScheduleBean
import com.pw.codeset.manager.ScheduleManager
import com.pw.codeset.utils.Constant
import com.pw.codeset.utils.DateJudge
import com.pw.codeset.utils.IntentUtils.toScheduleEdit
import com.pw.codeset.utils.ShareUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

class ScheduleFragmentCompose: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireActivity()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                body()
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun body(
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    // 使用 DisposableEffect 监听生命周期变化
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // 当进入 ON_RESUME 时调用 ViewModel 的刷新方法
                viewModel.refreshData()
            }
        }

        // 添加观察者
        lifecycleOwner.lifecycle.addObserver(observer)

        // 当 Composable 销毁时移除观察者，防止内存泄漏
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    var showFilterList by remember { mutableStateOf(true) }

    val current = LocalContext.current
    val timeFilterList = createScheduleTimeFilterList(current)
    val stateFilterList = createScheduleStateFilterList(current)
    viewModel.changeFilter(listOf(timeFilterList.first(),stateFilterList.first()))
    val filterMaps= mutableMapOf<Int, List<ScheduleFilterBean>>()
    filterMaps[Constant.SCHEDULE_FILTER_TYPE_TIME] = timeFilterList
    filterMaps[Constant.SCHEDULE_FILTER_TYPE_STATE] = stateFilterList

    val showList = viewModel.showingScheduleList.collectAsState()
    val editSelectedList = viewModel.mEditSelectedList.collectAsState()
    val isEditMode = viewModel.isEditMode.collectAsState()

    var showPutOffConfirmDialog by remember { mutableStateOf(false) }
    var putOffTarSchedule : ScheduleBean? = null




    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.activity_header_backcolor),
                    titleContentColor = colorResource(R.color.activity_header_textcolor)
                ),
                title = {
                    Text(text = stringResource(R.string.schedule_title))
                },
                actions = {
                    val current = LocalContext.current
                    IconButton(onClick = { showFilterList = !showFilterList }) {
                        Icon(imageVector = if (showFilterList) Icons.Filled.DateRange else Icons.Filled.List, contentDescription = null, tint = colorResource(R.color.activity_header_textcolor))
                    }

                    IconButton(onClick = { toScheduleEdit(current) }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null, tint = colorResource(R.color.activity_header_textcolor))
                    }
                }
            )
        },
        content = {innerPadding->
            Box(
                modifier = Modifier.padding(innerPadding)
            ) {
                if (showFilterList) {
                    filterContent(
                        isEditMode,
                        showList,
                        editSelectedList,
                        filterMaps,
                        {
                            viewModel.changeFilter(it)
                        },
                        {item,state->
                            when (state) {
                                Constant.SCHEDULE_STATE_FINISHED->viewModel.completeSchedule(item)
                                Constant.SCHEDULE_STATE_DELETED->viewModel.deleteSchedule(item)
                                Constant.SCHEDULE_STATE_UNDONE->viewModel.restoreSchedule(item)
                                Constant.SCHEDULE_ACTION_PUT_OFF-> {
                                    putOffTarSchedule = item
                                    showPutOffConfirmDialog = true
                                }

                            }
                        },
                        { viewModel.startEditMode()},
                        {item,checked->
                            viewModel.selectItemOnEdit(item)
                        },
                        {
                            viewModel.endEditMode(true)
                        },{
                            viewModel.endEditMode(false)
                        },

                    )
                }else{
                    content(isEditMode)
                }
                if (showPutOffConfirmDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showPutOffConfirmDialog=false
                            putOffTarSchedule = null
                        },
                        title = {
                            Text(text = "确认推迟?")
                        },
                        text = {
                            Text(text = "推迟后不可恢复，确认推迟？")
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    putOffTarSchedule?.let {
                                        viewModel.putOffSchedule(it)
                                    }
                                    showPutOffConfirmDialog=false
                                    putOffTarSchedule = null
                                }
                            ) {
                                Text(text = "确认")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    showPutOffConfirmDialog=false
                                    putOffTarSchedule = null
                                }
                            ) {
                                Text(text = "取消")
                            }
                        }
                    )
                }
            }
        }
    )
}


@Composable
fun filterContent(
    isEditMode : State<Boolean>,
    showList: State<List<ScheduleBean>>,
    editSeletedList: State<MutableList<ScheduleBean>>,
    filterMaps: Map<Int, List<ScheduleFilterBean>>,
    onFilterSelected: (ScheduleFilterBean) -> Unit,
    onItemStateChangeClick:(ScheduleBean,Int)->Unit,
    onItemLongClick:(ScheduleBean)-> Unit,
    onCheckChange: (ScheduleBean,Boolean) -> Unit,
    onDeleteClick:()-> Unit,
    onCompleteClick:()-> Unit,
    dateType: Int?=null
) {
    Column() {
        if (!isEditMode.value) {
            filters(filterMaps, onFilterSelected)
        }
        filterList(isEditMode,showList, editSeletedList,onItemStateChangeClick,onItemLongClick,onCheckChange, onDeleteClick,onCompleteClick, dateType )

    }
}

@Composable
fun editBtns(editSeletedList: State<MutableList<ScheduleBean>>,deleteBtnClick: () -> Unit, completeBtnClick: () -> Unit) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
        .padding(horizontal = 15.dp, vertical = 10.dp)
        .fillMaxWidth()) {
        Button(
            onClick = { deleteBtnClick() },
            enabled = !editSeletedList.value.isEmpty(),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.height(24.dp)
        ) {
            Text(stringResource(R.string.dialog_default_delete), fontSize = 12.sp)
        }
        Button(
            onClick = {completeBtnClick()},
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.height(24.dp)
        ) {
            Text(stringResource(R.string.dialog_default_cancel), fontSize = 12.sp)
        }
    }
}

@Composable
fun filters(
    filterMaps : Map<Int,List<ScheduleFilterBean>>,
    onFilterSelected:(ScheduleFilterBean)-> Unit
) {
    Column() {
        filterMaps.values.forEach {
            filterRow(
                it.first(),
                it,
                {onFilterSelected(it)}
            )
        }
    }
}

@Composable
fun filterRow(
    default: ScheduleFilterBean,
    list:List<ScheduleFilterBean>,
    onItemSelected: (ScheduleFilterBean)->Unit
) {
    var selectedFilter by remember { mutableStateOf(default) }
    LazyRow {
        items(list){
            FilterChip(
                modifier = Modifier
                    .padding(horizontal = 6.dp, vertical = 6.dp)
                    .height(24.dp),
                selected = (selectedFilter==it),
                onClick = {
                    selectedFilter = it
                    onItemSelected(it)
                },
                label = {
                    Text(it.text)
                }
            )
        }
    }
}


@Composable
fun filterList(
    isEditMode: State<Boolean>,
    showList: State<List<ScheduleBean>>,
    editSeletedList: State<MutableList<ScheduleBean>>,
    onItemStateChangeClick: (ScheduleBean, Int) -> Unit,
    onItemLongClick:(ScheduleBean)-> Unit,
    onCheckChange: (ScheduleBean,Boolean) -> Unit,
    onDeleteClick:()-> Unit,
    onCompleteClick:()-> Unit,
    dateType: Int?
) {
    var selectedItem: ScheduleBean? by remember { mutableStateOf(null) }
    if (isEditMode.value) {
        editBtns(editSeletedList,onDeleteClick,onCompleteClick)
    }
    if (isEditMode.value) {
        selectedItem = null
    }
    LazyColumn() {
        items(showList.value) { item ->
            ScheduleItem(
                isEditMode,
                item,
                {
                    if (selectedItem == item) {
                        selectedItem = null
                    }else {
                        selectedItem = item
                    }
                },
                {
                    onItemStateChangeClick(item, it)
                },
                selected = selectedItem == item,
                onItemLongClick = onItemLongClick,
                onCheckChange ={ onCheckChange(item, it)},
                dateType = dateType,
            )
        }
    }
}

@Composable
fun content(isEditMode: State<Boolean>) {
    var listOf = listOf(
        Constant.SCHEDULE_DATE_TYPE_EARLIER,
        Constant.SCHEDULE_DATE_TYPE_TODAY,
        Constant.SCHEDULE_DATE_TYPE_WEEK,
        Constant.SCHEDULE_DATE_TYPE_MONTH,
        Constant.SCHEDULE_DATE_TYPE_YEAR
    )
    LazyColumn(

    ) {
        itemsIndexed(listOf){index,item->
            ScheduleGroupItem(isEditMode,item)
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleGroupItem(isEditMode: State<Boolean>,dateType:Int) {
    var title = ""
    var tarScheduleList = MutableLiveData<List<ScheduleBean>>()
    when (dateType) {
        Constant.SCHEDULE_DATE_TYPE_EARLIER -> {
            title = stringResource(R.string.schedule_list_title_earlier)
            tarScheduleList = ScheduleManager.getInstance().earlierScheduleList
        }

        Constant.SCHEDULE_DATE_TYPE_TODAY ->{
            title = stringResource(R.string.schedule_list_title_today)
            tarScheduleList = ScheduleManager.getInstance().todayScheduleList
        }
        Constant.SCHEDULE_DATE_TYPE_WEEK -> {
            title = stringResource(R.string.schedule_list_title_week)
            tarScheduleList = ScheduleManager.getInstance().weekScheduleList
        }
        Constant.SCHEDULE_DATE_TYPE_MONTH ->{
            title = stringResource(R.string.schedule_list_title_month)
            tarScheduleList = ScheduleManager.getInstance().monthScheduleList
        }
        else -> {
            title = stringResource(R.string.schedule_list_title_year)
            tarScheduleList = ScheduleManager.getInstance().yearScheduleList
        }
    }
    val scheduleList by tarScheduleList.observeAsState(emptyList())
    ExpandableGroup(title = title) {
        Column {
            scheduleList.forEach {
                ScheduleItem(isEditMode , scheduleBean = it, onClick = {}, onIconClick = {}, onCheckChange = {}, onItemLongClick = {}, dateType = dateType)
            }
        }
    }
}

@Composable
fun ExpandableGroup(
    title: String,
    content: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val transition = updateTransition(
        targetState = isExpanded,
        label = "expandTransition"
    )

    // 🔽 箭头旋转
    val rotation by transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 300, easing = FastOutSlowInEasing)
        },
        label = "rotation"
    ) { expanded ->
        if (expanded) 180f else 0f
    }

    // 🔽 内容渐变
    val alpha by transition.animateFloat(
        label = "alpha"
    ) { expanded ->
        if (expanded) 1f else 0f
    }

    Column {
        // ===== Header =====
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 8.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, colorResource(R.color.border_color))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { isExpanded = !isExpanded }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium
                )

                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.rotate(rotation)
                )
            }
        }

        // ===== Content =====
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .graphicsLayer { this.alpha = alpha }
            ) {
                content()
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScheduleItem(
    isEditMode: State<Boolean>,
    scheduleBean: ScheduleBean,
    onClick: () -> Unit,
    onIconClick: (Int) -> Unit,
    selected: Boolean = false,
    onItemLongClick:(ScheduleBean)-> Unit,
    onCheckChange: (Boolean) -> Unit,
    dateType: Int? = null
) {
    var isChecked by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    Surface(
        modifier = Modifier
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    if (isEditMode.value) {
                        isChecked = !isChecked
                        onCheckChange(isChecked)
                    } else {
                        onClick()
                    }
                },
                onLongClick = { onItemLongClick(scheduleBean) }
            )
            .padding(6.dp),
    ) {
        Row() {
            if (isEditMode.value) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = {
                        isChecked = it
                        onCheckChange(it)
                    },
                    modifier = Modifier.size(24.dp)
                )
            }
            Column() {
                ScheduleItemView(scheduleBean, dateType)
                AnimatedVisibility(selected) {
                    ScheduleItemEditView(scheduleBean.status, onIconClick,{
                        clickToShare(scheduleBean)
                    })
                }
            }
        }
    }
}



fun clickToShare(scheduleBean: ScheduleBean) {
    ShareUtils.shareTextToWeChat(MyApp.getInstance(),scheduleBean.desc)
}

@Composable
fun ScheduleItemView(scheduleBean: ScheduleBean, dateType: Int?=null) {
    Card(modifier = Modifier.defaultMinSize(minHeight = 44.dp), colors = CardDefaults.cardColors(containerColor = Color("#05000000".toColorInt()))) {
        Row(Modifier.padding(6.dp)) {
            scheduleBean.title?.let {
                Text(text = scheduleBean.title)
            }
            val icon = when (scheduleBean.status) {
                Constant.SCHEDULE_STATE_UNDONE->Icons.Filled.DateRange
                Constant.SCHEDULE_STATE_FINISHED->Icons.Filled.Done
                Constant.SCHEDULE_STATE_DELETED->Icons.Filled.Delete
                else -> Icons.Filled.Notifications
            }
            Icon(imageVector = icon, contentDescription = null, Modifier
                .padding(horizontal = 10.dp)
                .size(16.dp))
            Text(text = scheduleBean.desc, modifier = Modifier
                .weight(1f)
                .padding(end = 10.dp))
            var toLocalDate =
                scheduleBean.tarDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            toLocalDate.dayOfWeek.getDisplayName(TextStyle.FULL,Locale.getDefault())

            val tarText = if (DateJudge.isToday(scheduleBean.tarTime)) {
                NStringUtils.dateConvert(scheduleBean.tarTime, Constant.DATA_PARTNER_WITH_LINE_TILE_TIME_ONLY)
            }else if (DateJudge.isThisWeek(scheduleBean.tarTime)) {
                toLocalDate.dayOfWeek.getDisplayName(TextStyle.FULL,Locale.getDefault())
            }else{
                NStringUtils.dateConvert(scheduleBean.tarTime, Constant.DATA_PARTNER_WITH_CHAR_WITHOUT_TIME)
            }
            Text(text = tarText)
        }
    }

}

@Composable
fun ScheduleItemEditView(state:Int,onIconClick:(Int)->Unit,onShareClick:()-> Unit) {
    Row(Modifier.padding(8.dp)) {
        if (state== Constant.SCHEDULE_STATE_UNDONE) {
            IconButton(onClick = { onIconClick(Constant.SCHEDULE_STATE_FINISHED)}) {
                Icon(
                    imageVector = Icons.Filled.Done, contentDescription = null, Modifier
                        .padding(horizontal = 10.dp)
                        .size(16.dp)
                )
            }
        }
        if (state == Constant.SCHEDULE_STATE_DELETED) {
            IconButton(onClick = { onIconClick(Constant.SCHEDULE_STATE_UNDONE)}) {
                Icon(
                    imageVector = Icons.Filled.Refresh, contentDescription = null, Modifier
                        .padding(horizontal = 10.dp)
                        .size(16.dp)
                )
            }
        }
        IconButton(onClick = { onIconClick(Constant.SCHEDULE_STATE_DELETED)}) {
            Icon(
                imageVector = Icons.Filled.Delete, contentDescription = null, Modifier
                    .padding(horizontal = 10.dp)
                    .size(16.dp)
            )
        }
        IconButton(onClick = {onShareClick()}) {
            Icon(
                imageVector = Icons.Filled.Share, contentDescription = null, Modifier
                    .padding(horizontal = 10.dp)
                    .size(16.dp)
            )
        }
        IconButton(onClick = {onIconClick(Constant.SCHEDULE_ACTION_PUT_OFF)}) {
            Icon(
                imageVector = Icons.Filled.PlayArrow, contentDescription = null, Modifier
                    .padding(horizontal = 10.dp)
                    .size(16.dp)
            )
        }
    }

}