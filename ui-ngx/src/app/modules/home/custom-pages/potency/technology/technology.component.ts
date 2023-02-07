import { DatePipe } from '@angular/common';
import { Component } from '@angular/core';
import { PotencyService } from '@app/core/http/custom/potency.service';
import { AppState } from '@app/core/public-api';
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { PageComponent } from '@app/shared/components/page.component';
import { Procedure, ProcedureParam, ProcessCard, RunningState } from '@app/shared/models/custom/potency.models';
import { EntityType, entityTypeTranslations, entityTypeResources } from '@app/shared/public-api';
import { Store } from '@ngrx/store';
import { differenceInCalendarDays } from 'date-fns';
import { map } from 'rxjs/operators';

@Component({
  selector: 'tb-technology',
  templateUrl: './technology.component.html',
  styleUrls: ['./technology.component.scss']
})
export class TechnologyComponent extends PageComponent {

  today = new Date();
  disabledDate = (current: Date): boolean => {
    return differenceInCalendarDays(current, this.today) > 0;
  };
  
  searchForm = {
    sCardNo: '',
    sMaterialName: '',
    sOrderNo: '',
    dateRange: []
  }

  public readonly ProcessCardTableConfig: EntityTableConfig<ProcessCard> = new EntityTableConfig<ProcessCard>();
  public readonly ProcedureTableConfig: EntityTableConfig<Procedure> = new EntityTableConfig<Procedure>();

  currCard: ProcessCard = null; // 当前选中的流程卡
  currProcedure: Procedure = null; // 当前选中的工序

  procedureParams: ProcedureParam[] = []; // 当前工序对应的参数
  currentTabIndex = 0; // 当前显示的工序参数标签索引
  chartData: RunningState = {};

  constructor(
    protected store: Store<AppState>,
    private potencyService: PotencyService,
    private datePipe: DatePipe
  ) {
    super(store);
    this.initProcedureTableConfig();
    this.initProcessCardTableConfig();
  }

  /**
   * 初始化流程卡列表配置
   */
  initProcessCardTableConfig() {
    this.ProcessCardTableConfig.entityType = EntityType.PROCESS_CARD;
    this.ProcessCardTableConfig.entityTranslations = entityTypeTranslations.get(EntityType.PROCESS_CARD);
    this.ProcessCardTableConfig.entityResources = entityTypeResources.get(EntityType.PROCESS_CARD);

    this.ProcessCardTableConfig.entityKey = 'sCardNo';
    this.ProcessCardTableConfig.addEnabled = false;
    this.ProcessCardTableConfig.searchEnabled = false;
    this.ProcessCardTableConfig.refreshEnabled = false;
    this.ProcessCardTableConfig.detailsPanelEnabled = false;
    this.ProcessCardTableConfig.padding = '0';
    this.ProcessCardTableConfig.titleVisible = false;
    this.ProcessCardTableConfig.groupActionEnabled = false;
    this.ProcessCardTableConfig.entitiesDeleteEnabled = false;

    this.ProcessCardTableConfig.columns.push(
      new EntityTableColumn<ProcessCard>('sCardNo', 'potency.process-card-no', '100px', (entity) => (entity.sCardNo || ''), () => ({}), false),
      new EntityTableColumn<ProcessCard>('sMaterialName', 'potency.material-name', '180px', (entity) => (entity.sMaterialName || ''), () => ({}), false),
      new EntityTableColumn<ProcessCard>('sColorName', 'potency.color', '80px', (entity) => (entity.sColorName || ''), () => ({}), false),
      new EntityTableColumn<ProcessCard>('sOrderNo', 'potency.order-no', '100px', (entity) => (entity.sOrderNo || ''), () => ({}), false),
      new DateEntityTableColumn<ProcessCard>('tCreateTime', 'common.created-time', this.datePipe, '130px', 'yyyy-MM-dd HH:mm:ss', false)
    );

    this.ProcessCardTableConfig.entitiesFetchFunction = pageLink => {
      let dateBegin: number, dateEnd: number;
      const dateRange = this.searchForm.dateRange;
      if (dateRange && dateRange.length === 2) {
        dateBegin = (dateRange[0] as Date).getTime();
        dateEnd = (dateRange[1] as Date).getTime();
      }
      const params = JSON.parse(JSON.stringify(this.searchForm));
      delete params.dateRange;
      return this.potencyService.getProcessCardList(pageLink, {
        ...params, dateBegin: dateBegin || '', dateEnd: dateEnd || ''
      }).pipe(map((res) => {
        if (!this.currCard) {
          this.currCard = res.data && res.data[0] ? res.data[0] : null;
        }
        return res;
      }));
    }

    this.ProcessCardTableConfig.dataLoaded = () => this.selectCard();

    this.ProcessCardTableConfig.handleRowClick = ($event, entity) => {
      if ($event) {
        $event.stopPropagation();
      }
      if (!entity) {
        return;
      }
      this.currCard = entity;
      this.selectCard();
      return true;
    }
  }

  /**
   * 初始化工序列表配置
   */
  initProcedureTableConfig() {
    this.ProcedureTableConfig.entityType = EntityType.PROCEDURE;
    this.ProcedureTableConfig.entityTranslations = entityTypeTranslations.get(EntityType.PROCEDURE);
    this.ProcedureTableConfig.entityResources = entityTypeResources.get(EntityType.PROCEDURE);

    this.ProcedureTableConfig.loadDataOnInit = false;
    this.ProcedureTableConfig.entityKey = 'sWorkingProcedureNo';
    this.ProcedureTableConfig.addEnabled = false;
    this.ProcedureTableConfig.searchEnabled = false;
    this.ProcedureTableConfig.refreshEnabled = false;
    this.ProcedureTableConfig.detailsPanelEnabled = false;
    this.ProcedureTableConfig.padding = '0';
    this.ProcedureTableConfig.titleVisible = false;
    this.ProcedureTableConfig.groupActionEnabled = false;
    this.ProcedureTableConfig.entitiesDeleteEnabled = false;

    this.ProcedureTableConfig.columns.push(
      new EntityTableColumn<Procedure>('sWorkingProcedureNo', 'potency.procedure-no', '100px', (entity) => (entity.sWorkingProcedureNo || ''), () => ({}), false),
      new EntityTableColumn<Procedure>('sWorkingProcedureName', 'potency.procedure-name', '120px', (entity) => (entity.sWorkingProcedureName || ''), () => ({}), false),
      new EntityTableColumn<Procedure>('sWorkerGroupName', 'potency.team-name', '80px', (entity) => (entity.sWorkerGroupName || ''), () => ({}), false),
      new EntityTableColumn<Procedure>('sWorkerName', 'potency.team-members', '120px', (entity) => (entity.sWorkerName || ''), () => ({}), false),
      new EntityTableColumn<Procedure>('nTrackQty', 'potency.capacity', '80px', (entity) => (entity.nTrackQty || ''), () => ({}), false),
      new DateEntityTableColumn<Procedure>('tStartTime', 'datetime.time-from', this.datePipe, '130px', 'yyyy-MM-dd HH:mm:ss', false),
      new DateEntityTableColumn<Procedure>('tEndTime', 'datetime.time-to', this.datePipe, '130px', 'yyyy-MM-dd HH:mm:ss', false)
    );

    this.ProcedureTableConfig.entitiesFetchFunction = pageLink => {
      return this.potencyService.getProcedureList(pageLink, {
        cardNo: this.currCard.sCardNo
      }).pipe(map((res) => {
        if (!this.currProcedure) {
          this.currProcedure = res.data && res.data[0] ? res.data[0] : null;
        }
        return res;
      }));
    }

    this.ProcedureTableConfig.dataLoaded = () => this.selectProcedure();

    this.ProcedureTableConfig.handleRowClick = ($event, entity) => {
      if ($event) {
        $event.stopPropagation();
      }
      if (!entity) {
        return;
      }
      this.currProcedure = entity;
      this.selectProcedure();
      return true;
    }
  }

  /**
   * 选择流程卡
   */
  selectCard() {
    if (!this.currCard) {
      return;
    }
    this.ProcessCardTableConfig.table.dataSource.selectEntity(this.currCard);
    this.ProcedureTableConfig.table.updateData();
  }

  /**
   * 选择工序
   */
  selectProcedure() {
    if (!this.currProcedure) {
      return;
    }
    this.ProcedureTableConfig.table.dataSource.selectEntity(this.currProcedure);
    const { uemEquipmentGUID, tStartTime, tEndTime } = this.currProcedure;
    this.potencyService.getProcedureParams({ uemEquipmentGUID, tStartTime, tEndTime }).subscribe(res => {
      this.currentTabIndex = 0;
      this.procedureParams = res || [];
      this.showProcedureParamLineChart();
    });
  }

  /**
   * 展示工序参数曲线图
   */
  showProcedureParamLineChart() {
    const { tStartTime, tEndTime } = this.currProcedure;
    const currProcedureParam = this.procedureParams[this.currentTabIndex];
    this.potencyService.getProcedureParamChartData({
      ...currProcedureParam, tStartTime, tEndTime
    }).subscribe(res => {
      this.chartData = {
        properties: [{
          title: currProcedureParam.key,
          tsKvs: res || []
        }]
      }
    });
  }

  /**
   * 查询
   */
  search() {
    this.ProcessCardTableConfig.table.resetSortAndFilter(true);
  }

  /**
   * 文本查询条件清空事件
   * @param key 条件字段
   */
  onClear(key: string) {
    this.searchForm[key] = '';
    this.search();
  }

}
