var maxLabels = 60;

var config = {
    type: 'line',
    data: {
        labels: [],
        datasets: [{
            label: "Istanbul",
            backgroundColor: window.chartColors.red,
            borderColor: window.chartColors.red,
            fill: false,
            data: []
        }, {
            label: "Tokyo",
            backgroundColor: window.chartColors.blue,
            borderColor: window.chartColors.blue,
            fill: false,
            data: []
        }, {
            label: "Moscow",
            backgroundColor: window.chartColors.green,
            borderColor: window.chartColors.green,
            fill: false,
            data: []
        }, {
            label: "Beijing",
            backgroundColor: window.chartColors.orange,
            borderColor: window.chartColors.orange,
            fill: false,
            data: []
        }, {
            label: "London",
            backgroundColor: "#8e5ea2",
            borderColor: "#8e5ea2",
            fill: false,
            data: []
        }]
    },
    options: {
        responsive: true,
        title:{
            display:false,
            text:"Numbers of logs of each city in each day"
        },
        scales: {
            xAxes: [{
                display: true,
                scaleLabel: {
                    display: false,
                    labelString: 'Timestamp'
                }
            }],
            yAxes: [{
                display: true,
                scaleLabel: {
                    display: true,
                    labelString: 'Number of Logs'
                }
            }]
        }
    }
};

window.onload = function() {
    var ctx = document.getElementById("canvas").getContext("2d");
    window.myLine = new Chart(ctx, config);

};


// Here I assume we receive messages in correct order
function addDataToChart(message){

    var splitMessage = message.split(" ");

    if (splitMessage.length == 3){
        var time = splitMessage[0];
        var city = splitMessage[1];
        var count = splitMessage[2];


        // Add point if new
        if (config.data.labels.indexOf(time) < 0)
            addOne(time);

        // Remove one point if already too many
        if (config.data.labels.length > maxLabels)
            removeOne();

        // Update point
        config.data.datasets.forEach(function(dataset, datasetIndex) {
            if (dataset.label == city){
                dataset.data.forEach(function(point, pointIdx) {
                    if (point.x == time) point.y = count;
                });
            }
        });

    } else {
        console.log(message);
    }
        window.myLine.update();
}


function addOne(timestamp) {

    config.data.labels.push(timestamp);
    config.data.datasets.forEach(function(dataset, datasetIndex) {
        dataset.data.push({x: timestamp, y: undefined});
    });

    window.myLine.update();
}

function removeOne() {

    config.data.labels.shift();
    config.data.datasets.forEach(function(dataset, datasetIndex) {
        dataset.data.shift();
    });

    window.myLine.update();
}