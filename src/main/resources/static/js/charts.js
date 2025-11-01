function renderDemoLineChart(){
  const ctx=document.getElementById('lineChart');
  if(!ctx) return;
  const labels=["Mon","Tue","Wed","Thu","Fri","Sat","Sun"];
  const data={labels,datasets:[{label:'Mood',data:[5,6,7,6,7,8,7],borderColor:'#0d6efd'},{label:'Stress',data:[6,5,5,6,5,4,4],borderColor:'#dc3545'}]};
  new Chart(ctx,{type:'line',data,options:{responsive:true,maintainAspectRatio:false}});
}
