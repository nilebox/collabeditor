String.prototype.insert = function (index, string) {
  if (index > 0)
    return this.substring(0, index) + string + this.substring(index, this.length);
  else
    return string + this;
};

String.prototype.splice = function(start, length, replacement) {
    return this.substr(0, start) + replacement + this.substr(start + length);
};

String.prototype.removeSubstring = function(start, length) {
    return this.substr(0, start) + this.substr(start + length);
};