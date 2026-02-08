import {AfterViewInit, ChangeDetectionStrategy, Component, effect, ElementRef, input, ViewChild} from '@angular/core';
import {DailyEventCount} from '@models/analytics.model';
import * as d3 from 'd3';

@Component({
  selector: 'app-users-daily-summary',
  imports: [],
  template: `
    <div #usersChart></div>
  `,
  styleUrl: './users-daily-summary.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UsersDailySummaryComponent implements AfterViewInit{
  userDailySummary = input<DailyEventCount[]>([]);

  @ViewChild('usersChart') private usersChartContainer!: ElementRef;

  constructor(){
    effect(()=>{
      this.createUserEventTypePieChart(this.userDailySummary(), this.usersChartContainer);
    })
  }

  ngAfterViewInit(): void{
    this.createUserEventTypePieChart(this.userDailySummary(), this.usersChartContainer);
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
}
