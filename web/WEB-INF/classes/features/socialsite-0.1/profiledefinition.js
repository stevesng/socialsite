
/**
 * @fileoverview Profile Definition object
 */

/**
 * Wraps profile definition data, makes definitions easily accessible.
 * @class
 */
socialsite.ProfileDefinition = function(data) {
    this.profileDefinition = data;
    this.sections = this.profileDefinition.sections;
    this.propertyMap = {};
    this.propertyObjectMap = {};
    this.propertyObjectCollectionMap = {};
    
    for (var i=0; i<this.profileDefinition.sections.length; i++) {
        var section = this.profileDefinition.sections[i];
        
        for (var j=0; j<section.propertyObjects.length; j++) {
            var propertyObject = section.propertyObjects[j];
            this.propertyObjectMap[propertyObject.name] = propertyObject;
            
            for (var k=0; k<propertyObject.properties.length; k++) {
                var property = propertyObject.properties[k];
                this.propertyMap[property.name] = property;
            }
        }
        
        for (var n=0; n<section.propertyObjectCollections.length; n++) {
            var propertyObjectCollection = section.propertyObjectCollections[n];
            this.propertyObjectCollectionMap[propertyObjectCollection.name] = propertyObjectCollection;
            
            for (var q=0; q<propertyObjectCollection.properties.length; q++) {
                var pocprop = propertyObjectCollection.properties[q];
                this.propertyMap[pocprop.name] = pocprop;
            }
        }
        
        for (var m=0; m<section.properties.length; m++) {
            var prop = section.properties[m];
            this.propertyMap[prop.name] = prop;
        }
    }
}

socialsite.ProfileDefinition.prototype.getPropertyDefinition = function(name) {
    return this.propertyMap[name];
}
    
socialsite.ProfileDefinition.prototype.getPropertyObjectDefinition = function(name) {
    return this.propertyObjectMap[name];
}

socialsite.ProfileDefinition.prototype.getPropertyObjectCollectionDefinition = function(name) {
    return this.propertyObjectCollectionMap[name];
}
