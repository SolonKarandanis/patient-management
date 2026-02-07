import {AfterViewInit, ChangeDetectionStrategy, Component, effect, ElementRef, inject, OnInit, ViewChild} from '@angular/core';
import {AnalyticsService} from './data/service/analytics.service';
import * as d3 from 'd3';
import {DailyEventCount, DailyPaymentSummary} from '@core/models/analytics.model';
import {JsonPipe} from '@angular/common';

@Component({
  selector: 'app-dashboard',
  imports: [JsonPipe],
  template: `
    <div class="flex flex-wrap">
      <div class="w-full xl:w-8/12 mb-12 xl:mb-0 px-4 text-black">
        <div #patientsChart></div>
        <div #usersChart></div>
        <div #paymentsChart></div>
      </div>
    </div>
  `,
  styleUrl: './dashboard.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DashboardComponent implements OnInit, AfterViewInit {
  @ViewChild('patientsChart') private patientsChartContainer!: ElementRef;
  @ViewChild('usersChart') private usersChartContainer!: ElementRef;
  @ViewChild('paymentsChart') private paymentsChartContainer!: ElementRef;

  private analyticsService = inject(AnalyticsService);

  private patientsDailySummary = this.analyticsService.patientsDailySummary;
  private userDailySummary = this.analyticsService.userDailySummary;
  private paymentDailySummary = this.analyticsService.paymentDailySummary;

  constructor() {
    effect(() => {
      this.createEventCountChart(this.patientsDailySummary(), this.patientsChartContainer);
      this.createUserEventTypePieChart(this.userDailySummary(), this.usersChartContainer);
      this.createPaymentSummaryChart(this.paymentDailySummary(), this.paymentsChartContainer);
    });
  }

  ngOnInit(): void {
    this.analyticsService.executeGetPatientsDailySummary();
    this.analyticsService.executeGetUserDailySummary();
    this.analyticsService.executeGetPaymentDailySummary();
  }

  ngAfterViewInit(): void {
    this.createEventCountChart(this.patientsDailySummary(), this.patientsChartContainer);
    this.createUserEventTypePieChart(this.userDailySummary(), this.usersChartContainer);
    this.createPaymentSummaryChart(this.paymentDailySummary(), this.paymentsChartContainer);
  }

  private createUserEventTypePieChart(data: DailyEventCount[], chartContainer: ElementRef): void {
    if (!data || data.length === 0 || !chartContainer) {
      return;
    }

    const element = chartContainer.nativeElement;
    d3.select(element).select('svg').remove();

    const aggregatedData = d3.rollup(data, v => d3.sum(v, d => d.totalEvents), d => d.eventType);
    const pieData = Array.from(aggregatedData, ([key, value]) => ({eventType: key, totalEvents: value}));

    const margin = {top: 20, right: 20, bottom: 20, left: 20};
    const width = 600 - margin.left - margin.right;
    const height = 400 - margin.top - margin.bottom;
    const radius = Math.min(width, height) / 2;

    const svg = d3.select(element).append('svg')
      .attr('width', width + margin.left + margin.right)
      .attr('height', height + margin.top + margin.bottom)
      .append('g')
      .attr('transform', `translate(${width / 2 + margin.left},${height / 2 + margin.top + 20})`);

    svg.append('text')
      .attr('x', 0)
      .attr('y', -height / 2 - margin.top / 2 )
      .attr('text-anchor', 'middle')
      .style('font-size', '16px')
      .style('font-weight', 'bold')
      .text('Daily User Event Actions');

    const color = d3.scaleOrdinal(d3.schemeCategory10);

    const pie = d3.pie<any>().value(d => d.totalEvents);
    const arc = d3.arc().innerRadius(0).outerRadius(radius);

    const arcs = svg.selectAll('.arc')
      .data(pie(pieData))
      .enter().append('g')
      .attr('class', 'arc');

    arcs.append('path')
      .attr('d', arc as any)
      .attr('fill', d => color(d.data.eventType));


    const outerArc = d3.arc()
      .innerRadius(radius * 0.9)
      .outerRadius(radius * 0.9);

    arcs.append('polyline')
      .attr('points', function(d: any) {
        const pos = outerArc.centroid(d);
        pos[0] = radius * 0.95 * (midAngle(d) < Math.PI ? 1 : -1);
        return [arc.centroid(d), outerArc.centroid(d), pos] as any;
      })
      .style('fill', 'none')
      .style('stroke', 'black')
      .style('stroke-width', '1px');

    arcs.append('text')
      .attr('transform', function(d: any) {
        const pos = outerArc.centroid(d);
        pos[0] = radius * (midAngle(d) < Math.PI ? 1 : -1);
        return `translate(${pos})`;
      })
      .attr('dy', '.35em')
      .style('text-anchor', function(d: any) {
        return midAngle(d) < Math.PI ? 'start' : 'end';
      })
      .style('font-size', '10px')
      .text(d => d.data.eventType);

    function midAngle(d: any) {
      return d.startAngle + (d.endAngle - d.startAngle) / 2;
    }

  }

  private createEventCountChart(data: DailyEventCount[], chartContainer: ElementRef): void {
    if (!data || data.length === 0 || !chartContainer) {
      return;
    }

    const element = chartContainer.nativeElement;
    d3.select(element).select('svg').remove();

    const margin = {top: 20, right: 20, bottom: 30, left: 40};
    const width = 600 - margin.left - margin.right;
    const height = 400 - margin.top - margin.bottom;

    const svg = d3.select(element).append('svg')
      .attr('width', width + margin.left + margin.right)
      .attr('height', height + margin.top + margin.bottom)
      .append('g')
      .attr('transform', `translate(${margin.left},${margin.top})`);

    const x = d3.scaleBand()
      .range([0, width])
      .padding(0.1);

    const y = d3.scaleLinear()
      .range([height, 0]);

    x.domain(data.map(d => new Date(d.eventDate).toLocaleDateString()));
    y.domain([0, d3.max(data, d => d.totalEvents) || 0]);

    svg.append('g')
      .attr('transform', `translate(0,${height})`)
      .call(d3.axisBottom(x))
      .selectAll('text')
      .style('text-anchor', 'end')
      .attr('dx', '-.8em')
      .attr('dy', '.15em')
      .attr('transform', 'rotate(-65)');

    svg.append('g')
      .call(d3.axisLeft(y));

    svg.selectAll('.bar')
      .data(data)
      .enter().append('rect')
      .attr('class', 'bar')
      .attr('x', d => x(new Date(d.eventDate).toLocaleDateString()) || 0)
      .attr('width', x.bandwidth())
      .attr('y', d => y(d.totalEvents) || 0)
      .attr('height', d => height - (y(d.totalEvents) || 0));
  }

  private createPaymentSummaryChart(data: DailyPaymentSummary[], chartContainer: ElementRef): void {
    if (!data || data.length === 0 || !chartContainer) {
      return;
    }

    const element = chartContainer.nativeElement;
    d3.select(element).select('svg').remove();

    const margin = {top: 20, right: 20, bottom: 30, left: 40};
    const width = 600 - margin.left - margin.right;
    const height = 400 - margin.top - margin.bottom;

    const svg = d3.select(element).append('svg')
      .attr('width', width + margin.left + margin.right)
      .attr('height', height + margin.top + margin.bottom)
      .append('g')
      .attr('transform', `translate(${margin.left},${margin.top})`);

    const x = d3.scaleBand()
      .range([0, width])
      .padding(0.1);

    const y = d3.scaleLinear()
      .range([height, 0]);

    x.domain(data.map(d => new Date(d.eventDate).toLocaleDateString()));
    y.domain([0, d3.max(data, d => d.totalPayments) || 0]);

    svg.append('g')
      .attr('transform', `translate(0,${height})`)
      .call(d3.axisBottom(x))
      .selectAll('text')
      .style('text-anchor', 'end')
      .attr('dx', '-.8em')
      .attr('dy', '.15em')
      .attr('transform', 'rotate(-65)');

    svg.append('g')
      .call(d3.axisLeft(y));

    svg.selectAll('.bar')
      .data(data)
      .enter().append('rect')
      .attr('class', 'bar')
      .attr('x', d => x(new Date(d.eventDate).toLocaleDateString()) || 0)
      .attr('width', x.bandwidth())
      .attr('y', d => y(d.totalPayments) || 0)
      .attr('height', d => height - (y(d.totalPayments) || 0));
  }
}
