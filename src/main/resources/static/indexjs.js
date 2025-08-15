const selectedOptions = Array.from(document.getElementById("typeFilter").selectedOptions)
    .map(o => o.value)
    .join(",");
let url = `/events/streams?limit=${limit}`;
if (selectedOptions) url += `&type=${selectedOptions}`;
