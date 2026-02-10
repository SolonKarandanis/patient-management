import {AfterViewInit, ChangeDetectionStrategy, Component, effect, ElementRef, input, ViewChild} from '@angular/core';
import {DailyPaymentSummary} from '@models/analytics.model';
import * as d3 from 'd3';

@Component({
  selector: 'app-payments-daily-summary',
  imports: [],
  template: `
    <div #paymentsChart></div>
  `,
  styleUrl: './payments-daily-summary.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PaymentsDailySummaryComponent implements AfterViewInit{
  paymentDailySummary = input<DailyPaymentSummary[]>([]);

  @ViewChild('paymentsChart') private paymentsChartContainer!: ElementRef;

  constructor() {
    effect(() => {
      this.createPaymentSummaryChart(this.paymentDailySummary(), this.paymentsChartContainer);
    });
  }

  ngAfterViewInit(): void {
    this.createPaymentSummaryChart(this.paymentDailySummary(), this.paymentsChartContainer);
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
