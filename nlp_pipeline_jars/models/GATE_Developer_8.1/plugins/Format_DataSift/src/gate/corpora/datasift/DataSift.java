/*
 *  Copyright (c) 1995-2014, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *  
 *  Mark A. Greenwood, 23/06/2014
 */

package gate.corpora.datasift;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataSift {
  private Interaction interaction;
  private Map<String,Object> other = new HashMap<String,Object>();
  
  public Interaction getInteraction() {
    return interaction;
  }
  
  public void setInteraction(Interaction interaction) {
    this.interaction = interaction;
  }
  
  @JsonAnyGetter
  public Map<String,Object> getFurtherData() {
      return other;
  }

  @JsonAnySetter
  public void setFurtherData(String name, Object value) {
      other.put(name, value);
  }
}
