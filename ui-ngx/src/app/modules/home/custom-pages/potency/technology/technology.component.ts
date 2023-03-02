import { DatePipe } from '@angular/common';
import { Component } from '@angular/core';
import { PotencyService } from '@app/core/http/custom/potency.service';
import { AppState, FileService } from '@app/core/public-api';
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { PageComponent } from '@app/shared/components/page.component';
import { Procedure, ProcedureParam, ProcessCard, RunningState } from '@app/shared/models/custom/potency.models';
import { EntityType, entityTypeTranslations, entityTypeResources, PageLink } from '@app/shared/public-api';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
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

  public readonly processCardTableConfig: EntityTableConfig<ProcessCard> = new EntityTableConfig<ProcessCard>();
  public readonly procedureTableConfig: EntityTableConfig<Procedure> = new EntityTableConfig<Procedure>();

  currCard: ProcessCard = null; // 当前选中的流程卡
  currProcedure: Procedure = null; // 当前选中的工序

  procedureParams: ProcedureParam[] = []; // 当前工序对应的参数
  currentTabIndex = 0; // 当前显示的工序参数标签索引
  chartData: RunningState = {};

  constructor(
    protected store: Store<AppState>,
    private potencyService: PotencyService,
    private datePipe: DatePipe,
    private translate: TranslateService,
    private fileService: FileService
  ) {
    super(store);
    this.initProcedureTableConfig();
    this.initProcessCardTableConfig();
  }

  /**
   * 初始化流程卡列表配置
   */
  initProcessCardTableConfig() {
    this.processCardTableConfig.componentsData = {};

    this.processCardTableConfig.entityType = EntityType.PROCESS_CARD;
    this.processCardTableConfig.entityTranslations = entityTypeTranslations.get(EntityType.PROCESS_CARD);
    this.processCardTableConfig.entityResources = entityTypeResources.get(EntityType.PROCESS_CARD);

    this.processCardTableConfig.entityKey = 'sCardNo';
    this.processCardTableConfig.addEnabled = false;
    this.processCardTableConfig.searchEnabled = false;
    this.processCardTableConfig.refreshEnabled = false;
    this.processCardTableConfig.detailsPanelEnabled = false;
    this.processCardTableConfig.padding = '0';
    this.processCardTableConfig.titleVisible = false;
    this.processCardTableConfig.groupActionEnabled = false;
    this.processCardTableConfig.entitiesDeleteEnabled = false;

    this.processCardTableConfig.columns.push(
      new EntityTableColumn<ProcessCard>('sCardNo', 'potency.process-card-no', '100px', (entity) => (entity.sCardNo || ''), () => ({}), false),
      new EntityTableColumn<ProcessCard>('sMaterialName', 'potency.material-name', '180px', (entity) => (entity.sMaterialName || ''), () => ({}), false),
      new EntityTableColumn<ProcessCard>('sColorName', 'potency.color', '80px', (entity) => (entity.sColorName || ''), () => ({}), false),
      new EntityTableColumn<ProcessCard>('sOrderNo', 'potency.order-no', '100px', (entity) => (entity.sOrderNo || ''), () => ({}), false),
      new DateEntityTableColumn<ProcessCard>('tCreateTime', 'common.created-time', this.datePipe, '130px', 'yyyy-MM-dd HH:mm:ss', false)
    );

    this.processCardTableConfig.entitiesFetchFunction = pageLink => {
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

    // 导出功能
    this.processCardTableConfig.componentsData.exportTableData = () => {
      let dateBegin: number, dateEnd: number;
      const dateRange = this.searchForm.dateRange;
      if (dateRange && dateRange.length === 2) {
        dateBegin = (dateRange[0] as Date).getTime();
        dateEnd = (dateRange[1] as Date).getTime();
      }
      const params = JSON.parse(JSON.stringify(this.searchForm));
      delete params.dateRange;
      this.potencyService.getProcessCardList(new PageLink(9999999, 0), {
        ...params, dateBegin: dateBegin || '', dateEnd: dateEnd || ''
      }).subscribe((res) => {
        const dataList = [];
        if (res.data.length > 0) {
          const titleKeys = ['potency.process-card-no', 'potency.material-name', 'potency.color', 'potency.order-no', 'common.created-time'];
          const titleNames = [];
          titleKeys.forEach(key => {
            titleNames.push(this.translate.instant(key));
          });
          dataList.push(titleNames);
          res.data.forEach(item => {
            dataList.push([item.sCardNo, item.sMaterialName, item.sColorName, item.sOrderNo, this.datePipe.transform(item.tCreateTime, 'yyyy-MM-dd HH:mm:ss')]);
          });
        }
        this.fileService.exportTable(this.translate.instant('potency.technology'), dataList).subscribe();
      })
    }

    this.processCardTableConfig.dataLoaded = () => this.selectCard();

    this.processCardTableConfig.handleRowClick = ($event, entity) => {
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
    this.procedureTableConfig.entityType = EntityType.PROCEDURE;
    this.procedureTableConfig.entityTranslations = entityTypeTranslations.get(EntityType.PROCEDURE);
    this.procedureTableConfig.entityResources = entityTypeResources.get(EntityType.PROCEDURE);

    this.procedureTableConfig.loadDataOnInit = false;
    this.procedureTableConfig.entityKey = 'sWorkingProcedureNo';
    this.procedureTableConfig.addEnabled = false;
    this.procedureTableConfig.searchEnabled = false;
    this.procedureTableConfig.refreshEnabled = false;
    this.procedureTableConfig.detailsPanelEnabled = false;
    this.procedureTableConfig.padding = '0';
    this.procedureTableConfig.titleVisible = false;
    this.procedureTableConfig.groupActionEnabled = false;
    this.procedureTableConfig.entitiesDeleteEnabled = false;

    this.procedureTableConfig.columns.push(
      new EntityTableColumn<Procedure>('sWorkingProcedureNo', 'potency.procedure-no', '100px', (entity) => (entity.sWorkingProcedureNo || ''), () => ({}), false),
      new EntityTableColumn<Procedure>('sWorkingProcedureName', 'potency.procedure-name', '120px', (entity) => (entity.sWorkingProcedureName || ''), () => ({}), false),
      new EntityTableColumn<Procedure>('sWorkerGroupName', 'potency.team-name', '80px', (entity) => (entity.sWorkerGroupName || ''), () => ({}), false),
      new EntityTableColumn<Procedure>('sWorkerName', 'potency.team-members', '120px', (entity) => (entity.sWorkerName || ''), () => ({}), false),
      new EntityTableColumn<Procedure>('nTrackQty', 'potency.capacity', '80px', (entity) => (entity.nTrackQty || ''), () => ({}), false),
      new DateEntityTableColumn<Procedure>('tStartTime', 'datetime.time-from', this.datePipe, '130px', 'yyyy-MM-dd HH:mm:ss', false),
      new DateEntityTableColumn<Procedure>('tEndTime', 'datetime.time-to', this.datePipe, '130px', 'yyyy-MM-dd HH:mm:ss', false)
    );

    this.procedureTableConfig.entitiesFetchFunction = pageLink => {
      return this.potencyService.getProcedureList(pageLink, {
        cardNo: this.currCard.sCardNo
      }).pipe(map((res) => {
        if (!this.currProcedure) {
          this.currProcedure = res.data && res.data[0] ? res.data[0] : null;
        }
        return res;
      }));
    }

    this.procedureTableConfig.dataLoaded = () => this.selectProcedure();

    this.procedureTableConfig.handleRowClick = ($event, entity) => {
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
    this.processCardTableConfig.table.dataSource.selectEntity(this.currCard);
    this.procedureTableConfig.table.updateData();
  }

  /**
   * 选择工序
   */
  selectProcedure() {
    if (!this.currProcedure) {
      return;
    }
    this.procedureTableConfig.table.dataSource.selectEntity(this.currProcedure);
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
    if (currProcedureParam) {
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
  }

  /**
   * 查询
   */
  search() {
    this.processCardTableConfig.table.resetSortAndFilter(true);
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
