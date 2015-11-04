var Network, RadialPlacement, activate, root;

root = typeof exports !== "undefined" && exports !== null ? exports : this;
// Activate selector button
activate = function(group, link) {
  d3.selectAll("#" + group + " a").classed("active", false);
  return d3.select("#" + group + " #" + link).classed("active", true);
};

$(function() {
  var myNetwork;
  myNetwork = Network();
  d3.selectAll("#layouts a").on("click", function(d) {
    var newLayout;
    newLayout = d3.select(this).attr("id");
    activate("layouts", newLayout);
    return myNetwork.toggleLayout(newLayout);earchTerm = $(this).val();
    return myNetwork.updateSearch(searchTerm);
  });
  return d3.json("data/call_me_al.json", function(json) {
    return myNetwork("#vis", json);
  });
});
